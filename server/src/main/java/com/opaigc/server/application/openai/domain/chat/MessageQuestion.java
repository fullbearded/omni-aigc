package com.opaigc.server.application.openai.domain.chat;

import com.opaigc.server.application.openai.service.OpenAiService;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述
 *
 * @author huhongda@fiture.com
 * @date 2023/3/28
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageQuestion {
	private MessageType messageType;
	private String message;
	private Date date;
	private List<OpenAiService.CompletionsRequest.Message> messages;

	public MessageQuestion(MessageType messageType, List<OpenAiService.CompletionsRequest.Message> messages) {
		this.messages = messages;
		this.date = new Date();
	}

	public MessageQuestion(MessageType messageType, String message) {
		this.messageType = messageType;
		this.message = message;
		this.date = new Date();
	}
}
