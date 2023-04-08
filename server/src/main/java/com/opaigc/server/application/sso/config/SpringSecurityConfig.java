package com.opaigc.server.application.sso.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.opaigc.server.application.sso.service.impl.UserDetailsServiceImpl;
import com.opaigc.server.application.user.service.impl.UserServiceImpl;
import com.opaigc.server.infrastructure.redis.RedisUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: Runner.dada
 * @date: 2020/12/5
 * @description: The Security configuration
 * <p>
 * EnableGlobalMethodSecurity
 * prePostEnabled: 启用 前置注解[@PreAuthorize,@PostAuthorize,..]
 * securedEnabled: 启用 安全注解 [@Secured]
 * jsr250Enabled: 启用 JSR-250注解 [@RolesAllowed..]
 **/
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Slf4j
public class SpringSecurityConfig {

	@Bean
	public BCryptPasswordEncoder encode() {
		return new BCryptPasswordEncoder();
	}

	@Bean("UserDetailsService")
	public UserDetailsService createUserDetailsService(UserServiceImpl userDomainService, RedisUtil redisUtil) {
		return new UserDetailsServiceImpl(userDomainService, redisUtil);
	}
}