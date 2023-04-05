package com.liankebang.omnichat.application.user.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liankebang.omnichat.application.user.domain.Promotion;
import com.liankebang.omnichat.application.user.domain.User;
import com.liankebang.omnichat.application.user.domain.UserPromotion;
import com.liankebang.omnichat.application.user.mapper.PromotionMapper;
import com.liankebang.omnichat.application.user.mapper.UserPromotionMapper;
import com.liankebang.omnichat.application.user.processor.ChargeProcessor;
import com.liankebang.omnichat.application.user.service.PromotionService;
import com.liankebang.omnichat.application.user.service.UserService;
import com.liankebang.omnichat.infrastructure.exception.AppException;
import com.liankebang.omnichat.infrastructure.http.CommonResponseCode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: Runner.dada
 * @date: 2023/4/4
 * @description:
 **/
@Slf4j
@Service
public class PromotionServiceImpl extends ServiceImpl<PromotionMapper, Promotion> implements PromotionService {

	private static final Duration PROMOTION_USAGE_QUANTITY_EXPIRE = Duration.of(30, ChronoUnit.DAYS);

	private final UserPromotionMapper userPromotionMapper;
	private final StringRedisTemplate redisTemplate;
	private final UserService userService;
	private final ApplicationContext applicationContext;

	@Autowired
	public PromotionServiceImpl(UserPromotionMapper userPromotionMapper, StringRedisTemplate redisTemplate,
															UserService userService, ApplicationContext applicationContext) {
		this.userPromotionMapper = userPromotionMapper;
		this.redisTemplate = redisTemplate;
		this.userService = userService;
		this.applicationContext = applicationContext;
	}

	@Override
	public PromotionDTO checkPromotion(CheckPromotionParam param) {
		Promotion promotion = lambdaQuery().eq(Promotion::getCode, param.getCode()).one();
		if (promotion == null) {
			throw new AppException(CommonResponseCode.PROMOTION_NOT_FOUND);
		}
		if (promotion.isOutOfTime() || promotion.getStatus() == Promotion.Status.UNAVAILABLE) {
			throw new AppException(CommonResponseCode.PROMOTION_INVALID);
		}
		checkAndCacheUsedQuantity(promotion, false);

		return PromotionDTO.builder().type(promotion.getType()).code(promotion.getCode()).name(promotion.getName()).build();
	}

	@Override
	@Transactional
	public ChargePromotionDTO usePromotion(ChargePromotionParam param) {
		checkPromotion(CheckPromotionParam.builder().code(param.getCode()).build());

		User user = userService.getByCodeOrElseThrow(param.getUserCode());
		Promotion promotion = lambdaQuery().eq(Promotion::getCode, param.getCode()).last("for update").oneOpt()
			.orElseThrow(() -> new AppException(CommonResponseCode.PROMOTION_NOT_FOUND));

		String processor = Optional.ofNullable(promotion.getRule()).map(ext -> (String) JSONPath.eval(ext, "$.useRule.processor"))
			.orElse(null);

		if (StringUtils.isNotBlank(processor)) {
			ChargeProcessor useProcessor = applicationContext.getBean(processor, ChargeProcessor.class);
			ChargeProcessor.Context context = ChargeProcessor.Context.builder().promotion(promotion).user(user).scene(param.getScene()).build();
			if (!useProcessor.off(context)) {
				throw CommonResponseCode.PROMOTION_USE_FAILED.toException();
			}
		}

		checkAndCacheUsedQuantity(promotion, false);

		UserPromotion userPromotion = UserPromotion.builder().userId(user.getId())
			.promotionId(promotion.getId())
			.promotionCode(promotion.getCode())
			.promotionName(promotion.getName())
			.scene(param.getScene())
			.chargeData((JSONObject) JSON.toJSON(param))
			.chargeAt(LocalDateTime.now())
			.status(UserPromotion.Status.USED)
			.createdBy(String.valueOf(user.getId()))
			.updatedBy(String.valueOf(user.getId()))
			.build();
		userPromotionMapper.insert(userPromotion);

		ChargePromotionDTO locked =
			ChargePromotionDTO.builder().code(promotion.getCode()).name(promotion.getName()).type(promotion.getType()).build();
		log.info("锁定兑换完成，兑换码:{}， 锁定结果:{}", promotion.getCode(), JSON.toJSONString(locked));
		return locked;
	}

	/**
	 * 检查兑换是否超出使用次数
	 *
	 * @param increment true: 增加一次使用次数, false: 仅检查
	 */
	private void checkAndCacheUsedQuantity(Promotion promotion, boolean increment) {
		String code = promotion.getCode();
		String redisKey = buildUsedQuantityKey(code);
		if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
			Long quantity = userPromotionMapper.selectCount(Wrappers.<UserPromotion>lambdaQuery()
				.eq(UserPromotion::getPromotionCode, code).eq(UserPromotion::getStatus, UserPromotion.Status.USED));
			redisTemplate.opsForValue().set(redisKey, String.valueOf(quantity));
		}
		redisTemplate.expire(redisKey, PROMOTION_USAGE_QUANTITY_EXPIRE);

		Integer usedQuantity = Integer.valueOf(Objects.requireNonNull(redisTemplate.opsForValue().get(redisKey)));
		if (promotion.normalizeUsageLimit() <= usedQuantity) {
			log.warn("兑换达到最大使用次数,兑换码:{}, 最大使用次数:{}, 当前使用次数：{}", code, promotion.normalizeUsageLimit(), usedQuantity);
			throw new AppException(CommonResponseCode.PROMOTION_USED);
		}
		if (increment) {
			Long quantity = redisTemplate.opsForValue().increment(redisKey);
			log.info("增加兑换使用次数，兑换码：{}, redisKey:{}, 增加之后总次数：{}", code, redisKey, quantity);
		}
	}

	private String buildUsedQuantityKey(String code) {
		return String.format("promotion:usage:quantity:%s", code);
	}
}
