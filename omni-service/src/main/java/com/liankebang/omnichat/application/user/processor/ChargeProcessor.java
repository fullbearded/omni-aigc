package com.liankebang.omnichat.application.user.processor;

import com.liankebang.omnichat.application.user.domain.Promotion;
import com.liankebang.omnichat.application.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Runner.dada
 * @date: 2023/4/4
 * @description:
 **/
public interface ChargeProcessor {
	/**
	 * 使用兑换码
	 *
	 * @param context
	 * @return
	 */
	boolean off(Context context);

	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	@Builder
	class Context {
		private Promotion promotion;
		private String scene;
		private User user;
	}

}
