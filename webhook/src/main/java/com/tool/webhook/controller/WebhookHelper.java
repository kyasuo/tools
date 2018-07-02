package com.tool.webhook.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.tool.webhook.SystemException;
import com.tool.webhook.controller.resource.Commit;
import com.tool.webhook.controller.resource.Event;
import com.tool.webhook.controller.resource.PullRequestEvent;
import com.tool.webhook.controller.resource.PushEvent;
import com.tool.webhook.service.Message;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Component
public class WebhookHelper {

	private final String[] TEMPLATES = { "push.ftl", "pullreq.ftl" };
	private final Map<String, Template> templateCache = new HashMap<String, Template>();

	public WebhookHelper(Configuration freemarkerConfiguration) {
		try {
			for (String template : TEMPLATES) {
				this.templateCache.put(template, freemarkerConfiguration.getTemplate(template));
			}
		} catch (Exception e) {
			throw new SystemException("template couldn't be loaded.", e);
		}
	}

	public Message convertToMessages(PushEvent event) {
		final Map<String, Object> params = new HashMap<String, Object>();
		putCommonParams(params, event);
		params.put("ref", event.getRef());
		List<String> commitList = new ArrayList<String>();
		for (Commit commit : event.getCommits()) {
			if (StringUtils.isNotBlank(commit.getMessage().trim())) {
				commitList.add(commit.getMessage().trim());
			}
		}
		params.put("commits", commitList);
		return createMessage("push.ftl", params);
	}

	public Message convertToMessages(PullRequestEvent event) {
		final Map<String, Object> params = new HashMap<String, Object>();
		putCommonParams(params, event);
		params.put("action", event.getAction());
		params.put("number", event.getNumber());
		params.put("title", event.getPull_request().getTitle());
		params.put("prUrl", event.getPull_request().getHtml_url());
		return createMessage("pullreq.ftl", params);
	}

	private void putCommonParams(Map<String, Object> params, Event event) {
		if (event.getRepository() != null) {
			params.put("name", event.getRepository().getFull_name());
			params.put("url", event.getRepository().getHtml_url());
		}
		if (event.getSender() != null) {
			params.put("sender", event.getSender().getLogin());
		}
	}

	private Message createMessage(String template, Map<String, Object> params) {
		final Message message = new Message();
		try {
			message.addMessage(FreeMarkerTemplateUtils.processTemplateIntoString(templateCache.get(template), params));
		} catch (Exception e) {
			List<String> keyValue = new ArrayList<String>();
			for (Entry<String, Object> entry : params.entrySet()) {
				keyValue.add(entry.getKey() + "=" + entry.getValue());
			}
			throw new SystemException("The message conversion is failed. template=" + template + ", params={"
					+ StringUtils.join(keyValue, ",") + "}", e);
		}
		return message;
	}
}
