package com.liankebang.omnichat.application.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liankebang.omnichat.application.user.domain.User;

import java.util.Optional;

/**
 * @author: Runner.dada
 * @date: 2020/12/21
 * @description:
 **/
public interface UserService extends IService<User> {

	User findById(Long id);

	Optional<User> getByUsername(String username);

	User getByCodeOrElseThrow(String code);

	Boolean delete(Long id);
}
