package com.opaigc.server.application.user.event.listener;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.opaigc.server.application.user.domain.Member;
import com.opaigc.server.application.user.domain.User;
import com.opaigc.server.application.user.domain.UserChat;
import com.opaigc.server.application.user.event.ChatStreamCompletedEvent;
import com.opaigc.server.application.user.service.MemberService;
import com.opaigc.server.application.user.service.UserChatService;
import com.opaigc.server.application.user.service.UserService;
import com.opaigc.server.infrastructure.utils.TokenCounter;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: Runner.dada
 * @date: 2020/12/13
 * @description: 用户验证事件监听
 **/
@Service
@Slf4j
public class ChatStreamCompletedSubscriber {
	private final static long CACHE_REFRESH_MINUTES = 10;
	private final static long CACHE_EXPIRE_MINUTES = 30;

	@Autowired
	private UserService userService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private UserChatService userChatService;

	LoadingCache<String, Optional<Member>> memberCache = CacheBuilder.newBuilder()
		.refreshAfterWrite(CACHE_REFRESH_MINUTES, TimeUnit.MINUTES)
		.expireAfterAccess(CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES)
		.maximumSize(1000L)
		.build(CacheLoader.from(code -> {
			User user = userService.getByCode(code).get();
			return memberService.findByUserId(user.getId());
		}));

	public void receiveMessage(String message) {
		log.info("ChatStreamCompletedEvent:{}", message);
		ChatStreamCompletedEvent event = JSONObject.parseObject(message, ChatStreamCompletedEvent.class);
		Optional<Member> memberOptional = memberCache.getUnchecked(event.getSessionId());
		if (memberOptional.isEmpty()) {
			return;
		}
		memberService.usedQuotaIncrement(memberOptional.get(), 1);

		TokenCounter tokenCounter = new TokenCounter();
		int tokenCount = tokenCounter.countMessages(event.getQuestions().getMessages());

		UserChat userChat = UserChat.builder().userId(memberOptional.get().getUserId())
			.token(tokenCount)
			.questions(JSONObject.parseObject(JSONObject.toJSONString(event.getQuestions())))
			.answers(new JSONObject().fluentPut("answer", event.getResponse()))
			.createdBy(event.getSessionId())
			.build();
		userChatService.save(userChat);
	}
}