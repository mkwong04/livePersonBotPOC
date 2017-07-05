package test.inquirybot.service.liveperson.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import test.inquirybot.service.liveperson.model.ChatEvents;

@Data
public class GetChatEventResponse {
	
	@JsonProperty("events")
	private ChatEvents events;

}
