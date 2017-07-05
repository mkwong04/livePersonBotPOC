package test.inquirybot.service.liveperson.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AgentSessionLocation {

	@JsonProperty("link")
	private Link link;
}
