package com.liankebang.omnichat.infrastructure.common;

/**
 * @author: Runner.dada
 * @date: 2020/12/13
 * @description: 全局常量
 **/
public class Constants {

	public static final String COPYRIGHT = "liankebang";

	public static final String AUTHORIZATION_HEADER = "Authorization";

	public static final String AUTHORIZATION_BEARER_PREFIX = "Bearer ";

	public static final String LOGIN_LOCKED_TIMES = "%s.login.times.%s";

	public static final String LOGIN_TOKEN = "%s.login.token.%s";

	public static final String LOGIN_LOCKED_VALUE = "login.locked";

	public static final String USER_LOGIN_PATH = "/api/login";

	public static final String[] STATIC_WHITELIST = {
		"/*.html", "/*.css", "/*.js", "/*.png", "/**/*.html", "/**/*.css", "/**/*.js", "/**/*.png",
		"/**/*.woff", "/**/*.ttf", "/**.json",
	};

	public static final String[] SWAGGER_WHITELIST = {
		"/v2/api-docs",
		"/swagger-resources",
		"/swagger-resources/**",
		"/swagger-ui.html",
	};

	public static final String[] URI_WHITELIST = {
		"/private/**",
		"/api/**"
	};

}
