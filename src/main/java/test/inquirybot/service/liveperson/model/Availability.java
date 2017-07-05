package test.inquirybot.service.liveperson.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Availability {

	@JsonProperty("chat")
	private String chat;
	
	@JsonProperty("voice")
	private String voice;
}
