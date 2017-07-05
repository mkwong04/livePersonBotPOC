package test.inquirybot.service.liveperson;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;
import test.inquirybot.service.AgentService;
import test.inquirybot.service.ChatService;
import test.inquirybot.service.exception.LivePersonApiClientException;
import test.inquirybot.service.exception.LivePersonChatAgentServiceException;
import test.inquirybot.service.restclient.LivePersonApiClient;

@Slf4j
public class LivePersonChatAgentServiceImpl implements AgentService{
	
	private LivePersonApiClient apiClient;
	
	private ChatService chatService;

	private String username;
	private String password;
	
	private String baseUrl;
	
	private String bearer;
	private String csrf;
	
	private String sessionId;
	
	/** agent related **/
	private String agentSessionId;
	private int maxChat = 0;
	
	private Timer timer;
	
	private static final String LIVE_PERSON_CHAT_AGENT_SERVICE = "agentVep";
	
	/**
	 * 
	 * @param restTemplate
	 * @param chatService
	 * @param livePersonApiUrl
	 * @param livePersonAccount
	 * @param livePersonUsername
	 * @param livePersonPassword
	 */
	public LivePersonChatAgentServiceImpl(LivePersonApiClient apiClient, 
										  ChatService chatService,
										  String livePersonUsername,
										  String livePersonPassword){
		this.apiClient = apiClient;
		this.chatService  = chatService;
		
		this.username = livePersonUsername;
		this.password = livePersonPassword;
		
		this.timer = new Timer();
	}
	
	/**
	 * @throws LivePersonChatAgentServiceException 
	 * 
	 */
	public void initDomain() throws LivePersonChatAgentServiceException {
		
		try {
			baseUrl = apiClient.getServiceApiDomain(LIVE_PERSON_CHAT_AGENT_SERVICE);
		} 
		catch (LivePersonApiClientException e) {
			throw new LivePersonChatAgentServiceException(e);
		}

		log.info("base URL : {}",baseUrl); 
	}

	/**
	 * invoke login API
	 */
	private boolean performLogin(){
		try {
			Map<String, String> loginResultMap = apiClient.login(this.baseUrl, this.username, this.password);
			
			this.sessionId 	= loginResultMap.get(LivePersonApiClient.LOGIN_SESSION_ID);
			this.bearer 	= loginResultMap.get(LivePersonApiClient.LOGIN_BEARER);
			this.csrf 		= loginResultMap.get(LivePersonApiClient.LOGIN_CSRF);

			log.info("session id: {}, bearer : {}, csrf : {}",this.sessionId, this.bearer, this.csrf);
			
			//TODO: start a timer task that refresh the session at regular interval
			RefreshTask refreshTask = new RefreshTask(apiClient, this.sessionId, this.csrf, this.baseUrl);
			timer.schedule(refreshTask, 1000, 1*60*1000); //1 sec first delay, every 2 min interval 
			
			return true;

		}
		catch (LivePersonApiClientException e) {
			log.error("failed login");
			return false;
		}
		
	}
	
	/**
	 * start chat session
	 * @param name
	 * @throws LivePersonChatAgentServiceException 
	 */
	public void startChatAgentSession(String name) throws LivePersonChatAgentServiceException{
		if(performLogin()){
			try{
				this.agentSessionId = apiClient.startAgentSession(this.bearer, this.baseUrl);
				
				log.info("Agent session id : {}", this.agentSessionId);
				
				Map<String, String> agentInfoMap = apiClient.getAgentInfo(this.bearer, this.agentSessionId, this.baseUrl);
	
				String maxChatStr = agentInfoMap.get(LivePersonApiClient.AGENT_INFO_MAX_CHAT);
				int maxChat = Integer.valueOf(maxChatStr);
				//TODO: spawn MaxChat thread to listen and handle chat
				InquiryBot bot = new InquiryBot(apiClient, this.bearer, this.agentSessionId, this.baseUrl, 
												this.chatService, 1*60*1000L, log);
				Thread agentThread = new Thread(bot);
				agentThread.start();
			}
			catch(LivePersonApiClientException lpace){
				log.error("Error starting bot thread", lpace);
			}
		}
		else{
			//TODO: error handling
		}
	}
	
	@PreDestroy
	public void cleanup(){
		log.info("clean up Live Person Chat Agent Service resource(s)...");
	}
	
	/**
	 * Refresh session task 
	 * @author minkeat.wong
	 *
	 */
	class RefreshTask extends TimerTask {
		
		private String sessionId;
		private String csrf;
		private String baseURI;
		private LivePersonApiClient apiClient;
		
		/**
		 * 
		 * @param apiClient
		 * @param sessionId from the response cookie of login API
		 * @param csrf from the response of login API
		 * @param baseURI
		 */
		public RefreshTask(LivePersonApiClient apiClient, String sessionId, String csrf, String baseURI){
			this.apiClient  = apiClient;
			this.sessionId 	= sessionId;
			this.csrf 		= csrf;
			this.baseURI	= baseURI;
		}

		@Override
		public void run() {
			try{
				apiClient.refreshLoginSession(sessionId, csrf, baseURI);
			}
			catch(LivePersonApiClientException lpace){
				log.warn("silently consume refresh session error",lpace);
			}
		}
	}
}
