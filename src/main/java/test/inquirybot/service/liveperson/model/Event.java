package test.inquirybot.service.liveperson.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Event {
	
	@JsonProperty("@id")
	@JsonInclude(value=Include.NON_EMPTY, content=Include.NON_NULL)
	private String id;

	/**
	 * sample value: state, line
	 * when type = state; state attribute has value
	 * when type = line; textType, text, by, source
	 */
	@JsonProperty("@type")
	private String type;

	@JsonProperty("time")
	@JsonInclude(value=Include.NON_EMPTY, content=Include.NON_NULL)
	private String time;
	
	/**
	 * only when type="line"
	 * sample value: plain
	 */
	@JsonProperty("textType")
	@JsonInclude(value=Include.NON_EMPTY, content=Include.NON_NULL)
	private String textType;
	
	@JsonProperty("text")
	@JsonInclude(value=Include.NON_EMPTY, content=Include.NON_NULL)
	private String text;
	
	/**
	 * only when type="line"
	 * sample value: info, visitor, agent
	 */
	@JsonProperty("by")
	@JsonInclude(value=Include.NON_EMPTY, content=Include.NON_NULL)
	private String by;
	
	/**
	 * only when type="line"
	 * sample value: system, visitor, agent
	 */
	@JsonProperty("source")
	@JsonInclude(value=Include.NON_EMPTY, content=Include.NON_NULL)
	private String source;
	
	/**
	 * only when type="line" and source = "system"
	 */
	@JsonProperty("systemMessageId")
	@JsonInclude(value=Include.NON_EMPTY, content=Include.NON_NULL)
	private String systemMessageId;
	
	/**
	 * only when type="state"
	 */
	@JsonProperty("state")
	@JsonInclude(value=Include.NON_EMPTY, content=Include.NON_NULL)
	private String state;
}
