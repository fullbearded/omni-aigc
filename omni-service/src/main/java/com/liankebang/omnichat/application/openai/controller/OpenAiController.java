package com.liankebang.omnichat.application.openai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.liankebang.omnichat.application.openai.domain.chat.MessageType;
import com.liankebang.omnichat.application.openai.service.OpenAiService;
import com.liankebang.omnichat.infrastructure.auth.AccountSession;
import com.liankebang.omnichat.infrastructure.http.ApiResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * @author: Runner.dada
 * @date: 2023/3/23
 * @description:
 **/
@Slf4j
@RestController
@RequestMapping({"/api"})
@RequiredArgsConstructor
public class OpenAiController {

	@Autowired
	private final MessageSource messageSource;
	@Autowired
	private OpenAiService openAiService;

	/**
	 * Chat 流式返回
	 */
	@PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
	@CrossOrigin(origins="*")
	public Flux<String> streamCompletions(@RequestBody OpenAiService.CompletionsRequest req, AccountSession accountSession) {
		if (Objects.isNull(req.getMessages()) || CollectionUtils.isEmpty(req.getMessages())) {
			return openAiService.chatSend(MessageType.TEXT, req.getPrompt(), accountSession.getCode());
		} else {
			return openAiService.chatSend(MessageType.TEXT, req.getMessages(), accountSession.getCode());
		}
	}

	@PostMapping("/dashboard/billing/credit")
	public ApiResponse creditGrants(@RequestBody CreditRequest req) {
		return ApiResponse.success(openAiService.creditGrants(req.getKey()));
	}

	@PostMapping("/chat/moderation")
	public ApiResponse moderation(@RequestBody @Valid ModerationRequest req) {
		OpenAiService.ModerationData moderation = openAiService.moderation(req.getPrompt());
		List<String> errors = new ArrayList<>();
		for (Map.Entry<String, Boolean> entry : moderation.getResults().get(0).getCategories().entrySet()) {
			String key = entry.getKey();
			Boolean value = entry.getValue();
			if (value) {
				errors.add(messageSource.getMessage("openai.moderation." + key, null, Locale.getDefault()));
			}
		}
		return ApiResponse.success(ModerationResponse.builder().errorMessages(errors).source(moderation).build());
	}

	@PostMapping("/chat/moderation/status")
	public ApiResponse checkContent(@RequestBody @Validated ModerationRequest req) {
		return ApiResponse.success(new JSONObject().fluentPut("status", openAiService.checkContent(req.getPrompt()).block()));
	}




	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ModerationResponse {
		private OpenAiService.ModerationData source;
		private List<String> errorMessages;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ModerationRequest {
		@NotNull(message = "prompt not null")
		@NotNull(message = "prompt not blank")
		private String prompt;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CreditRequest {
		private String key;
	}
}
