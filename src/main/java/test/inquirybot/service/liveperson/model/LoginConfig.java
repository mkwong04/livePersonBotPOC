package test.inquirybot.service.liveperson.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginConfig {

	@JsonProperty("loginName")
	private String loginName;
	
	@JsonProperty("userId")
	private String userId;
	
	@JsonProperty("userPid")
	private String userPid;
	
	@JsonProperty("userPriviledges")
	private List<Integer> userPrivileges = new ArrayList<>();
	
	@JsonProperty("serverCurrentTime")
	private Long serverCurrentTime;
	
	@JsonProperty("timeDiff")
	private Long timeDiff;
	
	@JsonProperty("serverTimeZoneName")
	private String serverTimeZoneName;
	
	@JsonProperty("serverTimeGMTDiff")
	private Long serverTimeGMTDiff;
	
	@JsonProperty("isLPA")
	private boolean isLPA;
	
	@JsonProperty("isAdmin")
	private boolean isAdmin;
	
	@JsonProperty("accountTimeZoneId")
	private String accountTimeZoneId;
}
