package test.inquirybot.service.liveperson.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import test.inquirybot.service.liveperson.model.AgentSessionLocation;

@Data
public class StartAgentSessionResponse {

	@JsonProperty("agentSessionLocation")
	private AgentSessionLocation agentSessionLocation;
}
