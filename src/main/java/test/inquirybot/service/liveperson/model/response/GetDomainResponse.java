package test.inquirybot.service.liveperson.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetDomainResponse {

	@JsonProperty("service")
	private String service;
	
	@JsonProperty("account")
	private String account;

	@JsonProperty("baseURI")
	private String baseURI;
}
