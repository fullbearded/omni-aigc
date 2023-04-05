package com.liankebang.omnichat.application.sso.domain.entity;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author: Runner.dada
 * @date: 2020/12/13
 * @description: Account Type
 **/
public enum AccountType {
	SCRM, WECHAT;

	public static AccountType valueOfName(String name) {
		return Arrays.stream(AccountType.values())
			.filter(type -> Objects.equals(name, type.name()))
			.findFirst().orElse(null);
	}
}
