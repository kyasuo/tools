package com.tool.gitbucket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tool.gitbucket.resources.PullRequest;
import com.tool.util.PropertyUtil;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class PullReqChecker {

	public static void main(String[] args) throws Exception {

		// get Pull/Reqs from Gitbucket
		final List<PullRequest> pullRequestList = getPullRequestList();

		// check Pull/Reqs whether or not they are mergeable
		final List<String> messages = checkPullRequests(pullRequestList);

		// send IRC if messages exist
		if (!messages.isEmpty()) {
			sendIRCMessage(messages);
		}
	}

	private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
	private static final String AUTHORIZATION = "token " + PropertyUtil.getProperty("oauth_token");
	private static final String ACCEPT = "application/json; charset=utf-8";
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static List<PullRequest> getPullRequestList() throws IOException {
		final List<PullRequest> pullRequestList = new ArrayList<PullRequest>();
		for (String url : PropertyUtil.getProperyListByPrefix("pulls.url.")) {
			Request request = new Request.Builder().url(url).addHeader("Authorization", AUTHORIZATION)
			        .addHeader("Accept", ACCEPT).build();
			pullRequestList.addAll(Arrays.asList(
			        MAPPER.readValue(HTTP_CLIENT.newCall(request).execute().body().byteStream(), PullRequest[].class)));
		}
		return pullRequestList;
	}

	private static List<String> checkPullRequests(List<PullRequest> pullRequestList) {
		final List<String> messages = new ArrayList<String>();
		final Set<String> mergeable = new HashSet<String>(PropertyUtil.getProperyListByPrefix("mergeable.branch."));
		for (PullRequest pullRequest : pullRequestList) {
			String base = pullRequest.getBase().getLabel().trim();
			String compare = pullRequest.getHead().getLabel().trim();
			if (!"master".equals(base) || mergeable.contains(compare)) {
				continue;
			}
			messages.add(" - P/R: #" + pullRequest.getNumber() + ", url: " + pullRequest.getHtml_url()
			        + ", repository: " + pullRequest.getBase().getRepo().getFull_name() + ", opener: "
			        + pullRequest.getUser().getLogin() + ", base: " + base + ", compare: " + compare);
		}
		if (!messages.isEmpty()) {
			messages.add(0, PropertyUtil.getProperty("message.prefix"));
			messages.add(PropertyUtil.getProperty("message.suffix"));
		}
		return messages;
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
