package com.opaigc.server.application.user.service.impl;


import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opaigc.server.application.user.domain.User;
import com.opaigc.server.application.user.mapper.UserMapper;
import com.opaigc.server.application.user.service.UserService;
import com.opaigc.server.infrastructure.exception.AppException;
import com.opaigc.server.infrastructure.http.CommonResponseCode;

import java.time.LocalTime;
import java.util.Optional;

/**
 * @author: Runner.dada
 * @date: 2020/12/6
 * @description: the system user domain service
 **/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

	@Override
	public User findById(Long id) {
		return lambdaQuery().eq(User::getId, id).one();
	}

	@Override
	public Optional<User> getByUsername(String username) {
		return lambdaQuery().eq(User::getUsername, username).eq(User::getStatus, User.UserStatusEnum.ENABLE).oneOpt();
	}

	@Override
	public User getByCodeOrElseThrow(String code) {
		return lambdaQuery().eq(User::getCode, code).eq(User::getStatus, User.UserStatusEnum.ENABLE)
			.last("LIMIT 1").oneOpt()
			.orElseThrow(() -> new AppException(CommonResponseCode.USER_NOT_FOUND));
	}

	@Override
	public Boolean delete(Long id) {
		return lambdaUpdate().eq(User::getId, id).set(User::getDeletedAt, LocalTime.now())
			.update();
	}
}
