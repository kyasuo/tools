package com.tool.gitbucket;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tool.gitbucket.resources.PullRequest;
import com.tool.util.PropertyUtil;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class PullReqIssuer {

	static final String EXECUTED_DATE = (new SimpleDateFormat("yyyy/MM/dd")).format(new Date());

	public static void main(String[] args) throws Exception {

		final List<String> messages = new ArrayList<String>();
		PullReqParam param;
		for (String groupRepository : PropertyUtil.getProperyListByPrefix("group.repository.label.branches.")) {
			param = createPullReqParam(groupRepository);

			// get Pull/Reqs from Gitbucket and derive target bases by them
			deriveTargetBases(getOpendPullRequestList(param.getApiUrl()), param.getBranches());

			// create Pull/Reqs
			messages.addAll(createPullRequests(param.getWebUrl(), param.getLabel(), param.getBranches()));
		}

		// send IRC if messages exist
		if (!messages.isEmpty()) {
			messages.add(0, PropertyUtil.getProperty("message.prefix"));
			messages.add(PropertyUtil.getProperty("message.suffix"));
			sendIRCMessage(messages);
		}
	}

	static class PullReqParam implements Serializable {
		private static final long serialVersionUID = 1L;
		private final String apiUrl;
		private final String webUrl;
		private final String group;
		private final String repository;
		private final List<String> branches;
		private final String label;

		public PullReqParam(String group, String repository, List<String> branches, String label, String apiUrl,
		        String webUrl) {
			super();
			this.apiUrl = apiUrl;
			this.webUrl = webUrl;
			this.branches = branches;
			this.group = group;
			this.repository = repository;
			this.label = label;
		}

		public String getApiUrl() {
			return apiUrl;
		}

		public String getWebUrl() {
			return webUrl;
		}

		public String getGroup() {
			return group;
		}

		public String getRepository() {
			return repository;
		}

		public List<String> getBranches() {
			return branches;
		}

		public String getLabel() {
			return label;
		}

	}

	private static PullReqParam createPullReqParam(String groupRepository) {
		String[] values = groupRepository.split(",");

		final String group = values[0].trim();
		final String repository = values[1].trim();
		final String label = values[2].trim();
		final List<String> branches = new ArrayList<String>();
		for (int i = 3; i < values.length; i++) {
			branches.add(values[i].trim());
		}
		String apiUrl = GITBUCKET_URL.endsWith("/") ? GITBUCKET_URL : GITBUCKET_URL + "/";
		String webUrl = apiUrl + group + "/" + repository + "/compare";
		apiUrl = apiUrl + "api/v3/repos/" + group + "/" + repository + "/pulls";
		return new PullReqParam(group, repository, branches, label, apiUrl, webUrl);
	}

	private static final String GITBUCKET_URL = PropertyUtil.getProperty("gitbucket.url");
	private static final String GITBUCKET_USER = PropertyUtil.getProperty("login.user");
	private static final String GITBUCKET_PASS = PropertyUtil.getProperty("login.pass");
	private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
	private static final String AUTHORIZATION = "token " + PropertyUtil.getProperty("oauth_token");
	private static final String ACCEPT = "application/json; charset=utf-8";
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static String addParameter(String url, String parameter) {
		if (!url.toLowerCase().contains(parameter)) {
			url = url + (url.contains("?") ? "&" : "?") + parameter;
		}
		return url;
	}

	private static List<String> createPullRequests(String url, String label, List<String> bases) throws Exception {
		final List<String> messages = new ArrayList<String>();
		if (bases.isEmpty()) {
			return messages;
		}
		// logon
		open(url);
		$("#userName").val(GITBUCKET_USER);
		$("#password").val(GITBUCKET_PASS);
		$$("input[type=submit]").first().click();

		for (String base : bases) {
			// new pull/req
			open(url);
			$$("button#test span.muted").get(1).click();
			$$("a[data-branch=\"" + base + "\"]").first().click();
			if ($$("#show-form").size() == 1) {
				$("#show-form").click();

				$$("input[name=title]").first().val(PropertyUtil.getProperty("title.prefix") + EXECUTED_DATE
				        + PropertyUtil.getProperty("title.suffix"));
				$$("textarea[name=content]").first()
				        .val(PropertyUtil.getProperty("body.prefix") + base + PropertyUtil.getProperty("body.suffix"));

				$$("button#test span.strong").get(5).click();
				$$("a[data-label-id=\"" + label + "\"]").first().click();
				$$("input[type=submit]").first().click();

				String location = null;
				int count = 0;
				while (count < 100) {
					location = getWebDriver().getCurrentUrl();
					if (!location.startsWith(url)) {
						break;
					}
					sleep(100);
					count++;
				}
				if (count == 100) {
					throw new IllegalStateException("failure to create a new pull/req");
				}
				messages.add(" - P/R: " + location);
			}
		}
		close();
		return messages;

	}

	private static List<PullRequest> getOpendPullRequestList(String url) throws IOException {
		Request request = new Request.Builder().url(addParameter(url, "state=open"))
		        .addHeader("Authorization", AUTHORIZATION).addHeader("Accept", ACCEPT).build();
		return Arrays.asList(
		        MAPPER.readValue(HTTP_CLIENT.newCall(request).execute().body().byteStream(), PullRequest[].class));
	}

	private static List<String> deriveTargetBases(List<PullRequest> pullRequestList, List<String> bases) {
		for (PullRequest pullRequest : pullRequestList) {
			String base = pullRequest.getBase().getLabel().trim();
			String compare = pullRequest.getHead().getLabel().trim();
			if ("master".equals(compare) && bases.contains(base)) {
				bases.remove(base);
			}
		}
		return bases;
	}

	private static final String SERVER = PropertyUtil.getProperty("irc.server");
	private static final Integer PORT = PropertyUtil.getPropertyInt("irc.port");
	private static final Charset ENCODING = Charset.forName(PropertyUtil.getProperty("irc.encoding"));
	private static final String CHANNEL = PropertyUtil.getProperty("irc.channel");
	private static final String USER = PropertyUtil.getProperty("irc.name");
	private static final String IRC_LS = "\r\n";

	private static void sendIRCMessage(List<String> messages) {
		Socket socket = null;
		BufferedWriter bw = null;
		try {
			socket = new Socket(SERVER, PORT);
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), ENCODING));
			bw.write("NICK " + USER + IRC_LS);
			bw.write("USER " + USER + " 8 * : " + USER + IRC_LS);
			bw.write("JOIN " + CHANNEL + IRC_LS);
			bw.flush();
			TimeUnit.SECONDS.sleep(1);
			for (String message : messages) {
				bw.write("PRIVMSG " + CHANNEL + " :" + message + IRC_LS);
			}
			bw.flush();
			TimeUnit.MILLISECONDS.sleep(5000 + 250 * messages.size());
			bw.write("QUIT");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
