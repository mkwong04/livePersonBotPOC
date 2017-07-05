package test.inquirybot.service.liveperson.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Link {

	@JsonProperty("@href")
	private String href;

	@JsonProperty("@rel")
	private String rel;

}
