package com.liankebang.omnichat.application.sso.presentation.event.listener;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.liankebang.omnichat.application.sso.common.utils.LoginUtil;
import com.liankebang.omnichat.application.sso.presentation.event.AccountAuthenticationFailureEvent;
import com.liankebang.omnichat.application.sso.presentation.event.AccountAuthenticationSuccessEvent;
import com.liankebang.omnichat.infrastructure.redis.RedisUtil;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: Runner.dada
 * @date: 2020/12/13
 * @description: 用户验证事件监听
 **/
@Component
@Slf4j
public class AccountAuthenticationListener {

	@Value("${login.max-fail-times}")
	private String maxFailTimes;

	@Value("${login.lock-time}")
	private String lockTime;

	@Value("${login.expire-time}")
	private String expireTime;

	private RedisUtil redisUtil;

	public AccountAuthenticationListener(RedisUtil redisUtil) {
		this.redisUtil = redisUtil;
	}

	@EventListener(AccountAuthenticationFailureEvent.class)
	public void accountAuthenticationFailureEventHandler(AccountAuthenticationFailureEvent event) {
		String username = event.getUsername();
		log.info("Update Lock For {}, {}", username, event);

		try {
			String key = LoginUtil.buildAccountLoginTimesKey(event.getAccountType(), event.getUsername());
			redisUtil.incrTimesOrLock(key, Integer.valueOf(maxFailTimes), Integer.valueOf(lockTime), TimeUnit.MINUTES);
		} catch (Exception e) {
			log.error("Update Login Lock Failure, event {}, msg {}, exception {}",
				event, e.getMessage(), e);
		}
	}


	@EventListener(AccountAuthenticationSuccessEvent.class)
	public void accountAuthenticationSuccessEventHandler(AccountAuthenticationSuccessEvent event) {
		clearAccountLoginTimes(event);
		storeTokenWithExpire(event);
	}

	private void clearAccountLoginTimes(AccountAuthenticationSuccessEvent event) {
		String username = event.getUsername();
		log.info("Clear User {} Login Times", username);

		String redisKey = LoginUtil.buildAccountLoginTimesKey(event.getAccountType(), event.getUsername());
		redisUtil.delete(redisKey);
	}

	private void storeTokenWithExpire(AccountAuthenticationSuccessEvent event) {
		log.info("Store Token For Username {}", event.getUsername());
		try {
			String key = LoginUtil.buildStoreTokenKey(event.getAccountType(), event.getUsername());

			redisUtil.set(key, event.getToken(), Integer.parseInt(expireTime), TimeUnit.DAYS);
		} catch (Exception e) {
			log.error("Store Token Failure event {}, msg {}, exception {}",
				event, e.getMessage(), e);
		}
	}
}
