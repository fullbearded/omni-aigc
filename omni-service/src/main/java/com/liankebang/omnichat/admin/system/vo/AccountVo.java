package com.liankebang.omnichat.admin.system.vo;


import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author: Runner.dada
 * @date: 2020/12/27
 * @description:
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountVo {

	private Long id;

	private String userCode;

	private String username;

	private String email;

	private String mobile;

	private String avatar;

	private String sex;

	private String userType;

	private String status;

	private String remark;

	private LocalDateTime createdAt;

}


