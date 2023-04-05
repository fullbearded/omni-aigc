package com.liankebang.omnichat.application.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liankebang.omnichat.application.user.domain.Promotion;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * promotion
 *
 * @author Sim@fiture.com 2021-11-18
 */
public interface PromotionService extends IService<Promotion> {

	/**
	 * 检查该促销是否可用
	 *
	 * @return
	 */
	PromotionDTO checkPromotion(CheckPromotionParam param);

	/**
	 * 锁定促销
	 */
	ChargePromotionDTO usePromotion(ChargePromotionParam param);

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class ChargePromotionDTO {
		private String code;
		private String name;
		private Promotion.Type type;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class ChargePromotionParam {

		/**
		 * 业务场景
		 */
		private String scene;
		/**
		 * 用户code
		 */
		private String userCode;
		/**
		 * 兑换码
		 */
		private String code;

		/**
		 * 渠道
		 **/
		private String channel;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class CheckPromotionParam {
		@NotBlank(message = "code cannot be empty")
		private String code;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class PromotionDTO {
		private String code;
		private String name;
		private Promotion.Type type;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class RevokeUserPromotionParam {
		private String bizNo;
		private String code;
		private String accountId;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class PromotionUseRule {
		private String processor;
	}
}
