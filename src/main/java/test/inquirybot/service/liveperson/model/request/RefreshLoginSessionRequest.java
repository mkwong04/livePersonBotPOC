package test.inquirybot.service.liveperson.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RefreshLoginSessionRequest {

	@JsonProperty("csrf")
	private String csrf;
}
