package com.liankebang.omnichat.admin.system.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * @author: Runner.dada
 * @date: 2020/12/16
 * @description: 创建系统用户VO
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSystemUserVo {
	@NotBlank
	private String username;
	@NotBlank
	private String password;

}
