package com.liankebang.omnichat.admin.system.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.liankebang.omnichat.admin.system.vo.SystemUserVo;
import com.liankebang.omnichat.application.user.domain.User;
import com.liankebang.omnichat.application.user.service.UserService;
import com.liankebang.omnichat.infrastructure.http.ApiResponse;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * @author runner.dada
 * @description system_users
 * @date 2021-02-01
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/users")
public class UsersController {

	private final UserService systemUsersService;

	@Autowired
	public UsersController(
		UserService systemUsersService) {
		this.systemUsersService = systemUsersService;
	}

	/**
	 * 创建或新增
	 */
	@PostMapping
	public ApiResponse save(@RequestBody SystemUserVo systemUserVo) {
		log.info("systemUsers:" + JSON.toJSONString(systemUserVo));
		User oldSystemUser = systemUsersService.findById(Long.valueOf(systemUserVo.getId()));
		if (Objects.nonNull(oldSystemUser)) {
			BeanUtils.copyProperties(systemUserVo, oldSystemUser);
			systemUsersService.updateById(oldSystemUser);
		} else {
			User systemUsers = new User();
			BeanUtils.copyProperties(systemUserVo, systemUsers);
			systemUsersService.save(systemUsers);
		}
		return ApiResponse.success("保存成功");
	}

	/**
	 * 删除
	 */
	@DeleteMapping("{id}")
	public ApiResponse delete(long id) {
		User systemUsers = systemUsersService.findById(id);
		if (Objects.nonNull(systemUsers)) {
			systemUsersService.delete(id);
			return ApiResponse.success("删除成功");
		} else {
			return ApiResponse.notFound();
		}
	}

	/**
	 * 查询
	 */
	@GetMapping("{id}")
	public ApiResponse find(long id) {
		User systemUsers = systemUsersService.findById(id);
		if (Objects.nonNull(systemUsers)) {
			return ApiResponse.success(systemUsers);
		} else {
			return ApiResponse.notFound();
		}
	}

	/**
	 * 列表查询
	 */
	@GetMapping
	public ApiResponse query(SystemUserVo vo, Pageable page) {
		log.info("page:" + JSON.toJSONString(page) + "-json:" + JSON.toJSONString(vo));
		return ApiResponse.success(systemUsersService.list());
	}

}



