package com.opaigc.server.application.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.opaigc.server.application.user.domain.App;

/**
 * 描述
 *
 * @author huhongda@fiture.com
 * @date 2023/4/9
 */
public interface AppService extends IService<App> {
	App getByCode(String appCode);
	Boolean usageIncrement(Long appId, Boolean isPaid, Integer amount);
}
