package com.opaigc.server.infrastructure.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;

/**
 * @author: Runner.dada
 * @date: 2023/4/5
 * @description: MybatisPlus配置
 **/
@Configuration
@EnableTransactionManagement
@MapperScan("com.opaigc.**.mapper")
public class MybatisPlusConfig {

	@Bean
	public OptimisticLockerInnerInterceptor optimisticLockerInterceptor() {
		return new OptimisticLockerInnerInterceptor();
	}
}
