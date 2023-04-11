package com.opaigc.server.application.user.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.opaigc.server.application.user.domain.App;
import com.opaigc.server.application.user.mapper.AppMapper;
import com.opaigc.server.application.user.service.AppService;

import java.util.concurrent.TimeUnit;

/**
 * 描述
 *
 * @author huhongda@fiture.com
 * @date 2023/4/11
 */

@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {
	private final static long CACHE_REFRESH_MINUTES = 10;
	private final static long CACHE_EXPIRE_MINUTES = 30;

	LoadingCache<String, App> memberCache = CacheBuilder.newBuilder().refreshAfterWrite(CACHE_REFRESH_MINUTES, TimeUnit.MINUTES)
		.expireAfterAccess(CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES).maximumSize(1000L).build(CacheLoader.from(
			code -> lambdaQuery().eq(App::getCode, code).eq(App::getStatus, App.StatusEnum.ENABLED).isNull(App::getDeletedAt).one()));

	@Override
	public App getByCode(String appCode) {
		if (appCode == null) {
			return null;
		}
		return memberCache.getUnchecked(appCode);
	}

	@Override
	public Boolean usageIncrement(Long appId, Boolean isPaid, Integer amount) {
		String sql;
		if (isPaid) {
			sql = "paid_use_count = paid_use_count + " + amount;
		} else {
			sql = "used_count = used_count + " + amount;
		}
		return lambdaUpdate().eq(App::getId, appId).setSql(sql).update();
	}
}
