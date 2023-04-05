package com.liankebang.omnichat.application.sso.presentation.event;

import org.springframework.context.ApplicationEvent;
import com.liankebang.omnichat.application.sso.domain.entity.AccountType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: Runner.dada
 * @date: 2020/12/13
 * @description: 用户验证成功事件
 **/
@Getter
@Setter
@ToString
public class AccountAuthenticationSuccessEvent extends ApplicationEvent {

	private AccountType accountType;
	private String username;
	private String token;

	public AccountAuthenticationSuccessEvent(Object source) {
		super(source);
	}
}
