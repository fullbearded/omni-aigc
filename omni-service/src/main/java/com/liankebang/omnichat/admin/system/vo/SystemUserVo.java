package com.liankebang.omnichat.admin.system.vo;

import com.liankebang.omnichat.infrastructure.annotation.QueryField;
import com.liankebang.omnichat.infrastructure.annotation.QueryField.Operator;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author runner.dada
 * @description system_users
 * @date 2021-02-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SystemUserVo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	@QueryField(field = "id")
	private Integer id;
	/**
	 * 用户编码
	 */
	@QueryField(field = "userCode")
	private String userCode;
	/**
	 * 用户名
	 */
	@QueryField(field = "username")
	private String username;
	/**
	 * 用户邮箱
	 */
	@QueryField(field = "email")
	private String email;
	/**
	 * 手机号码
	 */
	@QueryField(field = "mobile")
	private String mobile;
	/**
	 * 密码
	 */
	@QueryField(field = "password")
	private String password;
	/**
	 * 头像地址
	 */
	@QueryField(field = "avatar")
	private String avatar;
	/**
	 * 用户性别: man男 woman女 unknown未知
	 */
	@QueryField(field = "sex")
	private String sex;
	/**
	 * 用户类型: system 系统用户 wecom 企业微信用户
	 */
	@QueryField(field = "userType")
	private String userType;
	/**
	 * 帐号状态：enable 正常使用 banned 禁用
	 */
	@QueryField(field = "status")
	private String status;
	/**
	 * 最后登陆ip
	 */
	@QueryField(field = "lastLoginIp")
	private String lastLoginIp;
	/**
	 * 最后登陆时间
	 */
	@QueryField(field = "lastLoginDate", operator = Operator.BETWEEN)
	private List<Timestamp> lastLoginDate;
	/**
	 * 备注信息
	 */
	@QueryField(field = "remark")
	private String remark;
	/**
	 * 删除者
	 */
	@QueryField(field = "deletedBy")
	private String deletedBy;
	/**
	 * 删除时间
	 */
	@QueryField(field = "deletedAt", operator = Operator.BETWEEN)
	private List<Timestamp> deletedAt;
	/**
	 * 创建者
	 */
	@QueryField(field = "createdBy")
	private String createdBy;
	/**
	 * 创建时间
	 */
	@QueryField(field = "createdAt", operator = Operator.BETWEEN)
	private List<Timestamp> createdAt;
	/**
	 * 更新时间
	 */
	@QueryField(field = "updatedAt", operator = Operator.BETWEEN)
	private List<Timestamp> updatedAt;
	/**
	 * 更新者
	 */
	@QueryField(field = "updatedBy")
	private String updatedBy;

}
