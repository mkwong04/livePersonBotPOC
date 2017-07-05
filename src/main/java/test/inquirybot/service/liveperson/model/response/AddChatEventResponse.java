package test.inquirybot.service.liveperson.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import test.inquirybot.service.liveperson.model.ChatLocation;

@Data
public class AddChatEventResponse {
	@JsonProperty("chatEventLocation")
	private ChatLocation chatEventLocation;
}
