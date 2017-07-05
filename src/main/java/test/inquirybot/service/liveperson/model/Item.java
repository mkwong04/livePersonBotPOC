package test.inquirybot.service.liveperson.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Item {

	@JsonProperty("id")
	private int id;
	
	@JsonProperty("deleted")
	private boolean deleted;
	
	@JsonProperty("name")
	private String name;
}
