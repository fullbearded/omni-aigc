package com.opaigc.server.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.opaigc.server.application.user.event.listener.ChatStreamCompletedSubscriber;
import com.opaigc.server.infrastructure.common.Constants;

/**
 * 描述
 *
 * @author huhongda@fiture.com
 * @date 2023/4/9
 */
@Configuration
public class RedisConfig {

	@Bean
	LettuceConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory();
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		return new StringRedisTemplate(redisConnectionFactory);
	}

	@Bean
	public RedisMessageListenerContainer container(RedisConnectionFactory redisConnectionFactory,
																								 MessageListenerAdapter listenerAdapter) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic(Constants.CHAT_STREAM_COMPLETED_TOPIC));
		return container;
	}

	@Bean
	public MessageListenerAdapter listenerAdapter(ChatStreamCompletedSubscriber subscriber) {
		MessageListenerAdapter adapter = new MessageListenerAdapter(subscriber, "receiveMessage");
		adapter.setSerializer(new StringRedisSerializer());
		return adapter;
	}
}
