package com.opaigc.server.application.openai.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.opaigc.server.application.openai.client.OpenAiClient;
import com.opaigc.server.application.openai.domain.chat.MessageQuestion;
import com.opaigc.server.application.openai.domain.chat.MessageType;
import com.opaigc.server.application.openai.listener.OpenAISubscriber;
import com.opaigc.server.application.openai.service.OpenAiService;
import com.opaigc.server.application.user.event.ChatStreamCompletedEvent;
import com.opaigc.server.config.AppConfig;
import com.opaigc.server.infrastructure.common.Constants;

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
	@Autowired
	private StringRedisTemplate redisTemplate;

	@Override
	public Flux<String> chatSend(MessageType type, String prompt, String sessionId) {
		OpenAiClient openAiClient = buildClient();
		MessageQuestion userMessage = new MessageQuestion(MessageType.TEXT, prompt);
		List<Message> messages = List.of(Message.builder().role("user").content(prompt).build());
		return sendToOpenAi(sessionId, messages, openAiClient, userMessage);
	}

	/**
	 * @param type      消息类型
	 * @param messages  消息列表
	 * @param sessionId 会话id 目前是用户Code
	 * @return Flux<String>
	 * @see OpenAiService#chatSend(MessageType, List, String)
	 **/
	@Override
	public Flux<String> chatSend(MessageType type, List<Message> messages, String sessionId) {
		OpenAiClient openAiClient = buildClient();
		MessageQuestion userMessage = new MessageQuestion(MessageType.TEXT, messages);
		return sendToOpenAi(sessionId, messages, openAiClient, userMessage);
	}

	private Flux<String> sendToOpenAi(String sessionId, List<Message> messages,
																		OpenAiClient openAiClient, MessageQuestion userMessage) {
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
		ChatStreamCompletedEvent event = ChatStreamCompletedEvent.builder()
			.sessionId(sessionId)
			.questions(questions)
			.response(response)
			.build();
		redisTemplate.convertAndSend(Constants.CHAT_STREAM_COMPLETED_TOPIC, JSONObject.toJSONString(event));
		log.info("Chat Completed: {}", JSONObject.toJSONString(event));
	}

	@Override
	public void fail(String sessionId) {

	}
}
