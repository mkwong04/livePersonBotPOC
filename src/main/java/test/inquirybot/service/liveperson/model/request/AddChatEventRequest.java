package test.inquirybot.service.liveperson.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import test.inquirybot.service.liveperson.model.Event;

@Data
public class AddChatEventRequest {

	@JsonProperty("event")
	private Event event;
}
