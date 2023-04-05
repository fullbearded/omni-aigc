package com.liankebang.omnichat.application.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author: Runner.dada
 * @date: 2020/12/6
 * @description: System User Domain Object
 **/
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "users", autoResultMap = true)
public class User implements Serializable {

	/**
	 * 自增主键
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

	private String code;

	private String username;

	private String email;

	private String mobile;

	private String password;

	private String avatar;

	@Builder.Default
	private UserType userType = UserType.SYSTEM;

	@Builder.Default
	private Sex sex = Sex.MAN;

	@Builder.Default
	private UserStatusEnum status = UserStatusEnum.ENABLE;

	private String lastLoginIp;

	private LocalDateTime lastLoginDate;

	/**
	 * 备注
	 **/
	private String remark;

	/**
	 * 删除时间
	 */
	private LocalDateTime deletedAt;
	private String deletedBy;

	/**
	 * 创建时间
	 */
	private LocalDateTime createdAt;
	private String createdBy;

	/**
	 * 修改时间
	 */
	private LocalDateTime updatedAt;

	private String updatedBy;

	public enum Sex {
		MAN, WOMAN, UNKNOWN
	}

	@Getter
	public enum UserStatusEnum {
		ENABLE("正常"), BANNED("禁用");

		private String desc;

		UserStatusEnum(String desc) {
			this.desc = desc;
		}
	}


	@Getter
	public enum UserType {
		SYSTEM("系统用户"), WECOM("企业微信用户"), USER("普通用户");

		private String desc;

		UserType(String desc) {
			this.desc = desc;
		}
	}
}
