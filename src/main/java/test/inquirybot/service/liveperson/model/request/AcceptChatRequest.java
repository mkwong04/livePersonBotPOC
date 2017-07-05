package test.inquirybot.service.liveperson.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AcceptChatRequest {

	@JsonProperty(value="chat", defaultValue="start")
	private String chat;
}
