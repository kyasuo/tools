package com.tool.gitbucket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tool.gitbucket.resources.PullRequest;
import com.tool.util.PropertyUtil;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MasterWatcher {

	static final String EXECUTED_DATE = (new SimpleDateFormat("yyyy/MM/dd")).format(new Date());

	public static void main(String[] args) throws Exception {

		// parse arguments
		if (args.length != 2) {
			System.err.println("arguments length must be 2.");
			System.exit(1);
		}
		final String url = args[0];
		final List<String> bases = new ArrayList<String>(Arrays.asList(args[1].split(",")));

		// get Pull/Reqs from Gitbucket and derive target bases by them
		deriveTargetBases(getOpendPullRequestList(url), bases);

		// create Pull/Reqs
		final List<String> messages = createPullRequests(url, bases);

		// send IRC if messages exist
		if (!messages.isEmpty()) {
			messages.add(0, PropertyUtil.getProperty("message.prefix"));
			messages.add(PropertyUtil.getProperty("message.suffix"));
			sendIRCMessage(messages);
		}
	}

	private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
	private static final String AUTHORIZATION = "token " + PropertyUtil.getProperty("oauth_token");
	private static final String ACCEPT = "application/json; charset=utf-8";
	private static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static String addParameter(String url, String parameter) {
		if (!url.toLowerCase().contains(parameter)) {
			url = url + (url.contains("?") ? "&" : "?") + parameter;
		}
		return url;
	}

	private static String parseLocation(Response response) throws Exception {
		// parse location from response header
		for (String name : response.headers().names()) {
			if (name.toLowerCase().equals("location")) {
				return response.header(name);
			}
		}
		// parse location from response body
		String body = response.body().string();
		// FIXME wrong double-quote escaping
		body = body.substring(1, body.length() - 1).replace("\\\"", "\"");
		String location = MAPPER.readValue(body, PullRequest.class).getHtml_url();
		return location == null ? "" : location;
	}

	private static List<String> createPullRequests(String url, List<String> bases) throws Exception {
		// prepare post parameters
		final Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("title",
		        PropertyUtil.getProperty("title.prefix") + EXECUTED_DATE + PropertyUtil.getProperty("title.suffix"));
		paramMap.put("head", "master");

		// create pullrequests
		Request request;
		RequestBody requestBody;
		final List<String> locations = new ArrayList<String>();
		for (String base : bases) {
			paramMap.put("body",
			        PropertyUtil.getProperty("body.prefix") + base + PropertyUtil.getProperty("body.suffix"));
			paramMap.put("base", base);
			requestBody = RequestBody.create(MEDIA_TYPE, MAPPER.writeValueAsString(paramMap));
			request = new Request.Builder().url(url).post(requestBody).addHeader("Authorization", AUTHORIZATION)
			        .addHeader("Accept", ACCEPT).build();
			locations.add(" - P/R: " + parseLocation(HTTP_CLIENT.newCall(request).execute()));
		}
		return locations;
	}

	private static List<PullRequest> getOpendPullRequestList(String url) throws IOException {
		Request request = new Request.Builder().url(addParameter(url, "state=open"))
		        .addHeader("Authorization", AUTHORIZATION).addHeader("Accept", ACCEPT).build();
		return Arrays.asList(
		        MAPPER.readValue(HTTP_CLIENT.newCall(request).execute().body().byteStream(), PullRequest[].class));
	}

	private static void deriveTargetBases(List<PullRequest> pullRequestList, List<String> bases) {
		for (PullRequest pullRequest : pullRequestList) {
			String base = pullRequest.getBase().getLabel().trim();
			String compare = pullRequest.getHead().getLabel().trim();
			if ("master".equals(compare) && bases.contains(base)) {
				bases.remove(base);
			}
		}
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
