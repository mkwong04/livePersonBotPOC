package test.inquirybot.service.liveperson.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IncomingRequests {

	@JsonProperty("ringingCount")
	private String ringingCount;
	
	@JsonProperty("link")
	private Link link;
}
