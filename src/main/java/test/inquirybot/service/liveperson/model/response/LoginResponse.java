package test.inquirybot.service.liveperson.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import test.inquirybot.service.liveperson.model.AccountData;
import test.inquirybot.service.liveperson.model.CsdsCollectionResponse;
import test.inquirybot.service.liveperson.model.LoginConfig;

@Data
public class LoginResponse {

	@JsonProperty("csrf")
	private String csrf;
	
	@JsonProperty("wsuk")
	private String wsuk;
	
	@JsonProperty("config")
	private LoginConfig config;
	
	@JsonProperty("csdsCollectionResponse")
	private CsdsCollectionResponse csdsCollectionResponse;
	
	@JsonProperty("accountData")
	private AccountData accountData;
	
	@JsonProperty("sessionTTl")
	private String sessionTTl;
	
	@JsonProperty("bearer")
	private String bearer;
}
