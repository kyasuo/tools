package com.tool.webhook.service;

import java.nio.charset.Charset;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.output.OutputRaw;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IRCMessageServiceImpl implements MessageService, Runnable {

	private static final String REGEX_LINE_SEPARATOR = "(\r\n|\n)";

	@Value("${irc.server}")
	private String server;
	@Value("${irc.port:6667}")
	private String port;
	@Value("${irc.name:webhook}")
	private String name;
	@Value("${irc.channel:#talk}")
	private String channel;
	@Value("${irc.encoding:ISO-2022-JP}")
	private String ircEncoding;

	private PircBotX pircBotX = null;

	@Override
	public synchronized void send(Message message) {
		OutputRaw outputRaw = pircBotX.sendRaw();
		for (String msg : message.getMessageList()) {
			for (String line : msg.split(REGEX_LINE_SEPARATOR)) {
				outputRaw.rawLine("PRIVMSG " + channel + " :" + line);
			}
		}
	}

	@Override
	public void run() {
		Configuration configuration = new Configuration.Builder().setShutdownHookEnabled(true)
				.addServer(server, Integer.parseInt(port)).addAutoJoinChannel(channel).setName(name)
				.setAutoReconnect(true).setEncoding(Charset.forName(ircEncoding)).buildConfiguration();
		pircBotX = new PircBotX(configuration);
		try {
			pircBotX.startBot();
		} catch (Exception e) {
			throw new IllegalStateException("PircBotX couldn't start.", e);
		}
	}

	@PostConstruct
	public void start() {
		Thread t = new Thread(this);
		t.start();
	}

	@PreDestroy
	public void destory() {
		if (pircBotX != null) {
			pircBotX.stopBotReconnect();
			pircBotX.close();
			pircBotX = null;
		}
	}

}
