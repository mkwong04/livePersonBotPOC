package test.inquirybot.service.liveperson.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import test.inquirybot.service.liveperson.model.IncomingRequests;

@Data
public class GetChatRequestResponse {

	@JsonProperty("incomingRequests")
	private IncomingRequests incomingRequests;
}
