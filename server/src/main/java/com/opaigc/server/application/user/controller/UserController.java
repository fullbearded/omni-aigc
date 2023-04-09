package com.opaigc.server.application.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.opaigc.server.application.user.domain.Member;
import com.opaigc.server.application.user.domain.User;
import com.opaigc.server.application.user.service.MemberService;
import com.opaigc.server.application.user.service.UserService;
import com.opaigc.server.infrastructure.auth.AccountSession;
import com.opaigc.server.infrastructure.http.ApiResponse;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述
 *
 * @author huhongda@fiture.com
 * @date 2023/4/9
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private MemberService memberService;

	/**
	 * 获取兑换码分类
	 **/
	@PostMapping("/info")
	public ApiResponse info(@NotNull(message = "请登录后再操作") AccountSession session) {
		User user = userService.getByCode(session.getCode()).get();
		Member member = memberService.findOrCreateByUserId(user.getId());

		UserInfoDTO userInfoDTO = UserInfoDTO.builder().code(user.getCode()).username(user.getUsername())
			.status(user.getStatus()).userType(user.getUserType()).avatar(user.getAvatar()).email(user.getEmail()).build();

		userInfoDTO.setExpireDate(member.getExpireDate());
		userInfoDTO.setDailyLimit(member.getDailyLimit());
		userInfoDTO.setUsedQuota(member.getUsedQuota());
		userInfoDTO.setTotalQuota(member.getTotalQuota());
		return ApiResponse.success(userInfoDTO);
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	static class UserInfoDTO {
		/**
		 * 用户Code
		 **/
		private String code;
		/**
		 * 用户名
		 **/
		private String username;
		/**
		 * 状态
		 **/
		private User.UserStatusEnum status;
		/**
		 * 用户类型
		 **/
		private User.UserType userType;
		/**
		 * 头像
		 **/
		private String avatar;
		/**
		 * 邮箱
		 **/
		private String email;

		/**
		 * 会员到期日
		 **/
		private LocalDateTime expireDate;
		/**
		 * 每日限额
		 **/
		private Integer dailyLimit;
		/**
		 * 已使用额度
		 **/
		private Integer usedQuota;
		/**
		 * 总查询额度
		 **/
		private Integer totalQuota;

	}
}
