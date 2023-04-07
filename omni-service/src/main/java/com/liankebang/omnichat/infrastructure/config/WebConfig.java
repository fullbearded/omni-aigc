package com.liankebang.omnichat.infrastructure.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import com.liankebang.omnichat.infrastructure.auth.AccountSessionResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author: Runner.dada
 * @date: 2020/12/27
 * @description:
 **/
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {


	private final HandlerMethodArgumentResolver[] handlerMethodArgumentResolvers;

	@Autowired(required = false)
	public WebConfig(
		HandlerMethodArgumentResolver[] handlerMethodArgumentResolvers) {
		this.handlerMethodArgumentResolvers = handlerMethodArgumentResolvers;
	}


	/**
	 * 添加ArgumentResolver，Controller中直接获取参数
	 */
	@Override
	protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		super.addArgumentResolvers(argumentResolvers);
		if (Objects.nonNull(handlerMethodArgumentResolvers)) {
			argumentResolvers.addAll(Arrays.asList(handlerMethodArgumentResolvers));
		}
		// 用于从SpringSecurity中获取账户的session信息
		argumentResolvers.add(new AccountSessionResolver());
	}
}
