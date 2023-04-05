package com.liankebang.omnichat.application.user.processor.impl;


import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.liankebang.omnichat.application.user.mapper.PromotionMapper;
import com.liankebang.omnichat.application.user.mapper.UserPromotionMapper;
import com.liankebang.omnichat.application.user.processor.ChargeProcessor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: Runner.dada
 * @date: 2023/4/4
 * @description:
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class FrequencyUseProcessor implements ChargeProcessor {

	private final PromotionMapper promotionMapper;
	private final UserPromotionMapper userPromotionMapper;

	@Override
	@Transactional
	public boolean off(Context context) {
		return true;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	static class SubscriptionUseParam {

		private String itemCode;

		private Integer quantity;
	}
}
