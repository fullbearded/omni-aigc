package com.liankebang.omnichat.admin.system.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.liankebang.omnichat.admin.system.vo.AccountVo;
import com.liankebang.omnichat.application.user.domain.User;
import com.liankebang.omnichat.application.user.service.UserService;
import com.liankebang.omnichat.infrastructure.auth.AccountSession;
import com.liankebang.omnichat.infrastructure.http.ApiResponse;

import jakarta.validation.constraints.NotNull;
/**
 * @author: Runner.dada
 * @date: 2020/12/27
 * @description: 账户信息
 **/
@RestController
@RequestMapping("/api/admin/account")
public class AccountController {

	@Autowired
	private UserService userService;

	@GetMapping("/info")
	public ApiResponse getAccountInfo(@NotNull(message = "请登录后再操作") AccountSession accountSession) {
		User systemUser = userService.findById(accountSession.getId());
		AccountVo accountVo = new AccountVo();
		BeanUtils.copyProperties(systemUser, accountVo);
		return ApiResponse.success(accountVo);
	}
}
