package com.liankebang.omnichat.infrastructure.auth;

import org.springframework.web.servlet.HandlerInterceptor;
import com.liankebang.omnichat.infrastructure.jwt.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: Runner.dada
 * @date: 2020/12/27
 * @description: session拦截器
 **/
@Slf4j
public class AccountSessionInterceptor implements HandlerInterceptor {

	private static final String AUTH_HEADER = "Authorization";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String token = request.getHeader(AUTH_HEADER);

		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
		if (jwtTokenProvider.validateToken(token)) {
			AccountSession accountSession = AccountSession.builder()
				.username(jwtTokenProvider.getUsername(token))
				.build();

			return true;
		} else {
			return false;
		}
	}
}
