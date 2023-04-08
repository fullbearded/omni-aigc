package com.opaigc.server.application.openai.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.opaigc.server.application.openai.client.OpenAiClient;
import com.opaigc.server.application.openai.domain.chat.MessageQuestion;
import com.opaigc.server.application.openai.domain.chat.MessageType;
import com.opaigc.server.application.openai.listener.OpenAISubscriber;
import com.opaigc.server.application.openai.service.OpenAiService;
import com.opaigc.server.config.AppConfig;

import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 描述
 *
 * @author Runner.dada
 * @date 2023/3/23
 */
@Service
@Slf4j
public class OpenAiServiceImpl implements OpenAiService {

	@Autowired
	private AppConfig appConfig;

	@Override
	public Flux<String> chatSend(MessageType type, String prompt, String sessionId) {
		OpenAiClient openAiClient = buildClient();
		MessageQuestion userMessage = new MessageQuestion(MessageType.TEXT, prompt);
		List<CompletionsRequest.Message> messages = List.of(CompletionsRequest.Message.builder().role("user").content(prompt).build());
		return sendToOpenAi(sessionId, messages, openAiClient, userMessage);
	}

	@Override
	public Flux<String> chatSend(MessageType type, List<CompletionsRequest.Message> messages, String sessionId) {
		OpenAiClient openAiClient = buildClient();
		MessageQuestion userMessage = new MessageQuestion(MessageType.TEXT, messages);
		return sendToOpenAi(sessionId, messages, openAiClient, userMessage);
	}

	private Flux<String> sendToOpenAi(String sessionId, List<CompletionsRequest.Message> messages,
																		OpenAiClient openAiClient, MessageQuestion userMessage){
		return Flux.create(emitter -> {
			OpenAISubscriber subscriber = new OpenAISubscriber(emitter, sessionId, this, userMessage);
			Flux<String> openAiResponse =
				openAiClient.getChatResponse(appConfig.getApiToken(), sessionId, messages, null, null, null);
			openAiResponse.subscribe(subscriber);
			emitter.onDispose(subscriber);
		});
	}

	@Override
	public CreditGrantsResponse creditGrants(String key) {
		OpenAiClient client = buildClient();
		return client.getCredit(Objects.isNull(key) ? appConfig.getApiToken() : key).block();
	}

	@Override
	public ModerationData moderation(String prompt) {
		OpenAiClient client = buildClient();
		Mono<ModerationData> toMono = client.getModeration(appConfig.getApiToken(), prompt);
		return toMono.block();
	}

	@Override
	public Mono<Boolean> checkContent(String prompt) {
		OpenAiClient client = buildClient();
		return client.checkContent(appConfig.getApiToken(), prompt);
	}

	private OpenAiClient buildClient() {
		return new OpenAiClient(appConfig);
	}

	@Override
	public void completed(MessageQuestion questions, String sessionId, String response) {
		log.info("sessionId: {}, questions: {}, response: {}", sessionId, questions, response);
	}

	@Override
	public void fail(String sessionId) {

	}
}
