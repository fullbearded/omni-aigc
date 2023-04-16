package com.opaigc.server.application.user.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opaigc.server.application.user.domain.App;
import com.opaigc.server.infrastructure.auth.AccountSession;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述
 *
 * @author huhongda@fiture.com
 * @date 2023/4/9
 */
public interface AppService extends IService<App> {

	App getByCode(String appCode);

	Boolean usageIncrement(Long appId, Boolean isPaid, Integer amount);

	List<AppListDTO> publicApps(AppListParam req);

	Boolean like(AppLikeParam req);

	App create(AppCreateParam req);

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class AppCreateParam {
		/**
		 * APP类型
		 **/
		@NotBlank(message = "APP类型不能为空")
		private App.AppCategoryEnum category;
		/**
		 * APP名称
		 */
		@NotBlank(message = "APP名称不能为空")
		private String name;
		/**
		 * APP图标
		 */
		private String icon;
		/**
		 * APP描述
		 */
		private String description;
		/**
		 * APP是否允许连续会话
		 **/
		private Boolean chat;
		/**
		 * APP表单模板
		 * e
		 */
		@NotNull(message = "APP表单模板不能为空")
		@TableField(typeHandler = FastjsonTypeHandler.class)
		private JSONArray forms;
		/**
		 * APP预置问题模板
		 * e
		 */
		@NotBlank(message = "APP预置问题模板不能为空")
		@TableField(typeHandler = FastjsonTypeHandler.class)
		private JSONArray roles;

		private AccountSession session;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class AppLikeParam {
		/**
		 * APP编码
		 */
		@NotBlank(message = "APP编码不能为空")
		private String code;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class AppListParam {
		/**
		 * APP编码
		 */
		private String code;
		/**
		 * APP推荐
		 **/
		private App.RecommendEnum recommend;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class AppListDTO {
		/**
		 * APP编码
		 */
		private String code;
		/**
		 * APP类型
		 **/
		private App.AppCategoryEnum category;
		/**
		 * APP推荐
		 **/
		private App.RecommendEnum recommend;
		/**
		 * APP点赞
		 **/
		private Integer like;
		/**
		 * APP名称
		 */
		private String name;
		/**
		 * APP图标
		 */
		private String icon;
		/**
		 * APP描述
		 */
		private String description;
		/**
		 * APP表单模板
		 * e
		 */
		private JSONArray forms;
		/**
		 * APP状态
		 **/
		private App.StatusEnum status;
	}
}
