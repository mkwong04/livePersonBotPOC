package test.inquirybot.service.liveperson.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AgentGroupsData {

	@JsonProperty("items")
	private List<Item> items = new ArrayList<>();
	
	@JsonProperty("revision")
	private int revision;
}
