package com.liankebang.omnichat.infrastructure.jwt;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import com.liankebang.omnichat.infrastructure.common.Constants;
import com.liankebang.omnichat.infrastructure.exception.AppException;
import com.liankebang.omnichat.infrastructure.http.CommonResponseCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.lang.Collections;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.management.relation.Role;

/**
 * @author: Runner.dada
 * @date: 2020/12/13
 * @description: provide JWT token operation
 **/
@Component
public class JwtTokenProvider {

	@Value("${security.jwt.secret-key:secret-key}")
	private String secretKey;

	@Value("${security.jwt.expire-in:3600000}")
	private long expireInMilliseconds = 360000;

	public JwtTokenProvider() {

	}

	public String createToken(String username, List<Role> roles) {

		Claims claims = Jwts.claims().setSubject(username);

		if (!Collections.isEmpty(roles)) {
			// TODO: need add authorities
			claims.put("authorities",
				roles.stream().map(s -> new SimpleGrantedAuthority(null)).filter(
					Objects::nonNull).collect(Collectors.toList()));
		}
		Date now = new Date();
		Date validity = new Date(now.getTime() + expireInMilliseconds);

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setIssuer(Constants.COPYRIGHT)
			.setExpiration(validity)
			.signWith(SignatureAlgorithm.HS256, getSecretKey())
			.compact();
	}

	public String getSecretKey() {
		return Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	public String getUsername(String token) {
		return Jwts.parser().setSigningKey(getSecretKey())
			.parseClaimsJws(token).getBody().getSubject();
	}

	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader(Constants.AUTHORIZATION_HEADER);
		if (Objects.nonNull(bearerToken) && bearerToken
			.startsWith(Constants.AUTHORIZATION_BEARER_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(getSecretKey()).parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			throw new AppException(CommonResponseCode.ILLEGAL_REQUEST);
		}
	}

	public boolean invalidToken(String token) {
		return !validateToken(token);
	}
}
