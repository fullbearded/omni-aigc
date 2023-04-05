package com.liankebang.omnichat.application.sso.common.utils;

import com.liankebang.omnichat.application.sso.domain.entity.AccountType;
import com.liankebang.omnichat.infrastructure.common.Constants;

import io.netty.util.internal.StringUtil;
import java.util.Optional;

/**
 * @author: Runner.dada
 * @date: 2020/12/13
 * @description: Login Util
 **/
public class LoginUtil {

	public static String buildAccountLoginTimesKey(AccountType accountType, String username) {
		String name = Optional.ofNullable(accountType)
			.map(Enum::name).orElse(StringUtil.EMPTY_STRING);
		return String.format(Constants.LOGIN_LOCKED_TIMES, name, username);
	}

	public static String buildStoreTokenKey(AccountType accountType, String username) {
		String name = Optional.ofNullable(accountType)
			.map(Enum::name).orElse(StringUtil.EMPTY_STRING);
		return String.format(Constants.LOGIN_TOKEN, name, username);
	}
}
