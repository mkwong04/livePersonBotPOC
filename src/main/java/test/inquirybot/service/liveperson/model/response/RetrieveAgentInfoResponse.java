package test.inquirybot.service.liveperson.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import test.inquirybot.service.liveperson.model.Info;

@Data
public class RetrieveAgentInfoResponse {
	
	@JsonProperty("info")
	private Info info;
}
