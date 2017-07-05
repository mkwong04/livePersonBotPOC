package test.inquirybot.service.liveperson.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Info {

	@JsonProperty("link")
	private Link link;
	
	@JsonProperty("agentName")
	private String agentName;
	
	@JsonProperty("displayName")
	private String displayName;
	
	@JsonProperty("loginName")
	private String loginName;
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("maxChats")
	private String maxChats;
	
	@JsonProperty("availability")
	private Availability availability;
}
