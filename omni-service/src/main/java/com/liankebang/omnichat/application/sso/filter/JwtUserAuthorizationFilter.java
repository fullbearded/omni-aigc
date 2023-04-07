package com.liankebang.omnichat.application.sso.filter;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.liankebang.omnichat.application.sso.domain.AccountType;
import com.liankebang.omnichat.application.sso.filter.AbstractJwtAuthorizationFilter;
import com.liankebang.omnichat.infrastructure.jwt.JwtTokenProvider;
import com.liankebang.omnichat.infrastructure.redis.RedisUtil;

/**
 * @author: Runner.dada
 * @date: 2020/12/13
 * @description:
 **/
public class JwtUserAuthorizationFilter extends AbstractJwtAuthorizationFilter {


	public JwtUserAuthorizationFilter(
		AuthenticationManager authenticationManager, RedisUtil redisUtil,
		JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
		super(authenticationManager, redisUtil, jwtTokenProvider, userDetailsService);
	}

	@Override
	protected String getAccountType() {
		return AccountType.USER.name();
	}
}
