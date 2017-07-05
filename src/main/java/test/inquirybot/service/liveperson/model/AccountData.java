package test.inquirybot.service.liveperson.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AccountData {

	@JsonProperty("agentGroupsData")
	private AgentGroupsData agentGroupsData;
}
