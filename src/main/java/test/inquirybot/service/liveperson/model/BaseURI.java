package test.inquirybot.service.liveperson.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BaseURI {

	@JsonProperty("account")
	private String account;
	
	@JsonProperty("baseURI")
	private String baseURI;
	
	@JsonProperty("service")
	private String service;
}
