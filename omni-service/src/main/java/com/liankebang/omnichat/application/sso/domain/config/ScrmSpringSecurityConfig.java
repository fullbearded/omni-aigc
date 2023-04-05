package com.liankebang.omnichat.application.sso.domain.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import com.liankebang.omnichat.application.sso.domain.filter.JwtUserAuthenticationFilter;
import com.liankebang.omnichat.application.sso.domain.filter.JwtUserAuthorizationFilter;
import com.liankebang.omnichat.infrastructure.common.Constants;
import com.liankebang.omnichat.infrastructure.exception.AppException;
import com.liankebang.omnichat.infrastructure.http.ApiResponse;
import com.liankebang.omnichat.infrastructure.http.CommonResponseCode;
import com.liankebang.omnichat.infrastructure.jwt.JwtTokenProvider;
import com.liankebang.omnichat.infrastructure.redis.RedisUtil;

import cn.hutool.json.JSONUtil;
import io.jsonwebtoken.ExpiredJwtException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.web.SecurityFilterChain;

/**
 * @author: Runner.dada
 * @date: 2020/12/13
 * @description: Scrm config，for System User Security
 **/
@Configuration
@Import(SpringSecurityConfig.class)
@Slf4j
public class ScrmSpringSecurityConfig extends SecurityFilterChain {

	private final UserDetailsService userDetailsService;

	private final PasswordEncoder passwordEncoder;

	private final ApplicationContext applicationContext;

	private final RedisUtil redisUtil;

	private final JwtTokenProvider jwtTokenProvider;

	@Autowired(required = false)
	public ScrmSpringSecurityConfig(
		@Qualifier("UserDetailsService") UserDetailsService userDetailsService,
		PasswordEncoder passwordEncoder, ApplicationContext applicationContext,
		RedisUtil redisUtil, JwtTokenProvider jwtTokenProvider) {
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
		this.applicationContext = applicationContext;
		this.redisUtil = redisUtil;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable()
			.headers().frameOptions().disable().and()
			.logout().disable().formLogin().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.exceptionHandling().authenticationEntryPoint(getExceptionHandlingHandler()).and()

			.authorizeRequests()
			.antMatchers(HttpMethod.GET, Constants.STATIC_WHITELIST).permitAll()
			.antMatchers(Constants.SWAGGER_WHITELIST).anonymous()
			.antMatchers(Constants.URI_WHITELIST).permitAll()
			.anyRequest().authenticated()
			.and()
			.addFilter(new JwtUserAuthenticationFilter(authenticationManager(), applicationContext,
				Constants.USER_LOGIN_PATH, jwtTokenProvider))
			.addFilter(new JwtUserAuthorizationFilter(authenticationManager(), redisUtil,
				jwtTokenProvider, userDetailsService))
		;
	}

	private AuthenticationEntryPoint getExceptionHandlingHandler() {
		log.info("Exception for authorization");
		return (request, response, exception) -> {
			CommonResponseCode code;

			Class<?> aClass = request.getAttribute("javax.servlet.error.exception").getClass();
			if (ExpiredJwtException.class.equals(aClass)) {
				code = CommonResponseCode.LOGIN_EXPIRED;
			} else if (AppException.class.equals(aClass)) {
				code = CommonResponseCode.ACCOUNT_BANNED;
			} else {
				code = CommonResponseCode.ACCESS_DENIED;
			}

			ApiResponse resp = ApiResponse.error(code);
			response.setStatus(resp.getStatus());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.getWriter().write(JSONUtil.toJsonStr(resp));
		};
	}
}
