package test.inquirybot.service.liveperson.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CsdsCollectionResponse {
	
	@JsonProperty("baseURIs")
	private List<BaseURI> baseURIs = new ArrayList<>();
}
