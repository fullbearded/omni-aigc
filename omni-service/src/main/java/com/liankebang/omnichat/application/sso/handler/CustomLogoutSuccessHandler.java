package com.liankebang.omnichat.application.sso.handler;

import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import com.liankebang.omnichat.application.sso.domain.AccountType;
import com.liankebang.omnichat.application.sso.event.AccountLogoutSuccessEvent;
import com.liankebang.omnichat.infrastructure.common.Constants;
import com.liankebang.omnichat.infrastructure.http.ApiResponse;
import com.liankebang.omnichat.infrastructure.jwt.JwtTokenProvider;

import cn.hutool.json.JSONUtil;
import io.netty.util.internal.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述
 *
 * @author huhongda@fiture.com
 * @date 2023/4/6
 */

@Component
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	private final ApplicationContext applicationContext;
	private final JwtTokenProvider jwtTokenProvider;

	public CustomLogoutSuccessHandler(ApplicationContext applicationContext,
																		JwtTokenProvider jwtTokenProvider) {
		this.applicationContext = applicationContext;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

		String authorization = request.getHeader(Constants.AUTHORIZATION_HEADER);
		if (Objects.isNull(authorization)) {
			responseJson(response, ApiResponse.success());
			return;
		}

		String authToken = authorization.replace(Constants.AUTHORIZATION_BEARER_PREFIX, StringUtil.EMPTY_STRING);
		String username = jwtTokenProvider.getUsername(authToken);
		if (StringUtil.isNullOrEmpty(username)) {
			responseJson(response, ApiResponse.success());
			return;
		}

		AccountLogoutSuccessEvent event = new AccountLogoutSuccessEvent(username);
		event.setAccountType(AccountType.USER);
		event.setUsername(username);
		applicationContext.publishEvent(event);

		responseJson(response, ApiResponse.success());
	}

	private void responseJson(HttpServletResponse response, ApiResponse apiResponse) throws IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write(JSONUtil.toJsonStr(apiResponse));
	}
}
