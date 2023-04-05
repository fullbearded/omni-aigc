package com.liankebang.omnichat.application.user.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.liankebang.omnichat.application.user.domain.Promotion;
import com.liankebang.omnichat.application.user.service.PromotionService;
import com.liankebang.omnichat.infrastructure.http.ApiResponse;
import com.liankebang.omnichat.infrastructure.utils.CodeUtil;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: Runner.dada
 * @date: 2023/4/5
 * @description:	兑换码
**/
@Slf4j
@RestController
@RequestMapping("/api/promotion")
public class PromotionController {

	private static final int CODE_DUPLICATE_MAX_RETRY_COUNT = 15;

	@Autowired
	private PromotionService promotionService;

	/**
	 * 获取兑换码分类
	 **/
	@PostMapping("/types")
	public ApiResponse types() {
		List<PromotionTypeDTO> list = new ArrayList<>();
		for (Promotion.Type type : Promotion.Type.values()) {
			list.add(PromotionTypeDTO.builder().type(type).description(type.getDescription()).dailyUsageLimit(type.getDailyUsageLimit())
				.totalCapacity(type.getTotalCapacity()).effectiveDays(type.getEffectiveDays()).paymentMethod(type.getPaymentMethod())
				.paymentValue(type.getPaymentValue()).price(type.getPrice())
				.build());
		}
		return ApiResponse.success(list);
	}

	/**
	 * 兑换码校验
	 **/
	@PostMapping("/check")
	public ApiResponse check(@RequestBody @Valid PromotionService.CheckPromotionParam param) {
		PromotionService.PromotionDTO promotion = promotionService.checkPromotion(param);
		return ApiResponse.success(promotion);
	}

	/**
	 * 兑换码兑换
	 **/
	@PostMapping("/charge")
	public ApiResponse charge(@RequestBody @Validated ChargePromotionParam param) {
		PromotionService.ChargePromotionDTO usePromotionDTO = promotionService.usePromotion(PromotionService.ChargePromotionParam.builder()
			.userCode(param.getUserCode())
			.code(param.getCode())
			.channel(param.getChannel())
			.build());
		return ApiResponse.success(usePromotionDTO);
	}

	/**
	 * 列表展示
	 */
	@PostMapping("/list")
	public ApiResponse list(@RequestBody @Validated ListPromotionParam req) {

		List<Promotion> promotions = promotionService.lambdaQuery()
			.eq(Objects.nonNull(req.getCode()), Promotion::getCode, req.getCode())
				.in(Objects.nonNull(req.getCodes()), Promotion::getCode, req.getCode())
					.like(Objects.nonNull(req.getName()), Promotion::getName, req.getName())
			.eq(Objects.nonNull(req.getType()), Promotion::getType, req.getType())
			.eq(Objects.nonNull(req.getStatus()), Promotion::getStatus, req.getStatus())
			.in(Objects.nonNull(req.getStatuses()), Promotion::getStatus, req.getStatuses())
			.eq(Objects.nonNull(req.getUsageLimit()), Promotion::getUsageLimit, req.getUsageLimit())
			.last("LIMIT 500").list();
		return ApiResponse.success(promotions);
	}

	/**
	 * 批量生成兑换码
	 **/
	@PostMapping("/create/batch")
	public ApiResponse batchCreate(@RequestBody @Valid BatchCreateParam param) {
		List<String> result = Lists.newArrayList();

		IntStream.range(0, param.getCount()).forEach(index -> {
			int retryCount = 0;
			while (retryCount < CODE_DUPLICATE_MAX_RETRY_COUNT) {
				String code = CodeUtil.generateNewCode(param.getType().getCodePrefix(), param.getType().getCodeLength());
				try {
					Promotion promotion =
						Promotion.builder().code(code).name(param.getName()).type(param.getType()).status(Promotion.Status.AVAILABLE)
							.usageLimit(param.getUsageLimit()).rule(new JSONObject()
								.fluentPut("useRule", param.getUseRule())
								.fluentPut("summary", param.getSummary())
								.fluentPut("scene", param.getScene())
								.fluentPut("source", param.getSource()))
							.startAt(LocalDateTime.now())
							.createdBy(param.getOperator())
							.build();
					promotionService.save(promotion);
					result.add(promotion.getCode());
					return;
				} catch (DuplicateKeyException e) {
					log.warn("生成兑换码失败，code已被占用 code={}。重新生成", code);
					retryCount++;
				} catch (Exception e) {
					log.error("生成兑换码异常，code = {}", code);
					throw e;
				}
			}
		});
		return ApiResponse.success(result);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class ListPromotionParam {
		/**
		 * 促销码
		 */
		private String code;

		private List<String> codes;

		/**
		 * 促销名称
		 */
		private String name;

		/**
		 *
		 */
		private String type;

		/**
		 * 促销状态，unavailable 不可用，available 可用
		 */
		private String status;

		private List<String> statuses;

		/**
		 * 使用次数
		 */
		private Integer usageLimit;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	static class PromotionTypeDTO {
		private Promotion.Type type;
		private String description;
		private String codePrefix;
		private int codeLength;
		private int dailyUsageLimit;
		private int effectiveDays;
		private int totalCapacity;
		private int price;
		private String paymentMethod;
		private String paymentValue;
	}


	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class ChargePromotionParam {
		/**
		 * 兑换码
		 **/
		@NotBlank(message = "兑换码不能为空")
		private String code;
		/**
		 * 用户Code
		 **/
		@NotBlank(message = "用户Code不能为空")
		private String userCode;
		/**
		 * 核销渠道
		 **/
		private String channel;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BatchCreateParam {
		/**
		 * 批量生成兑换码的最大数量
		 **/
		@NotNull
		private Integer count;
		/**
		 * 兑换码类型
		 **/
		@NotNull
		private Promotion.Type type;
		/**
		 * 兑换码名称
		 **/
		private String name;
		/**
		 * 兑换码使用规则
		 **/
		private PromotionService.PromotionUseRule useRule;
		/**
		 * 兑换码使用场景
		 **/
		private String scene;
		/**
		 * 兑换码使用说明
		 **/
		private String summary;
		/**
		 * 兑换码生成来源
		 **/
		private String source;
		/**
		 * 单个兑换码最大使用次数
		 **/
		@Builder.Default
		private Integer usageLimit = 1;
		/**
		 * 操作人
		 **/
		@NotBlank
		private String operator;
	}
}
