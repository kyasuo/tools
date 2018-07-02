package com.tool.webhook.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tool.webhook.controller.resource.PullRequestEvent;
import com.tool.webhook.controller.resource.PushEvent;
import com.tool.webhook.service.MessageService;

@RestController
@RequestMapping("webhook/api/v1")
public class WebhookRestController {

	private final MessageService messageService;
	private final WebhookHelper webhookHelper;

	public WebhookRestController(MessageService messageService, WebhookHelper webhookHelper) {
		this.messageService = messageService;
		this.webhookHelper = webhookHelper;
	}

	@RequestMapping(headers = "X-Github-Event=push")
	@ResponseStatus(HttpStatus.OK)
	public void push(@RequestBody PushEvent event) {
		messageService.send(webhookHelper.convertToMessages(event));
	}

	@RequestMapping(headers = "X-Github-Event=pull_request")
	@ResponseStatus(HttpStatus.OK)
	public void push(@RequestBody PullRequestEvent event) {
		messageService.send(webhookHelper.convertToMessages(event));
	}

}
