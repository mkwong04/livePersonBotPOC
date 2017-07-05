package test.inquirybot.service.liveperson.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ChatEvents {

	@JsonProperty("link")
	private Links links;
	
	@JsonProperty("event")
	private Events events;
}
