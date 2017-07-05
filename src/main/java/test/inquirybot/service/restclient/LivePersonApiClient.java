package test.inquirybot.service.restclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import test.inquirybot.service.exception.LivePersonApiClientException;
import test.inquirybot.service.liveperson.model.Event;
import test.inquirybot.service.liveperson.model.request.AcceptChatRequest;
import test.inquirybot.service.liveperson.model.request.AddChatEventRequest;
import test.inquirybot.service.liveperson.model.request.LoginRequest;
import test.inquirybot.service.liveperson.model.request.RefreshLoginSessionRequest;
import test.inquirybot.service.liveperson.model.request.StartAgentSessionRequest;
import test.inquirybot.service.liveperson.model.response.AcceptChatResponse;
import test.inquirybot.service.liveperson.model.response.AddChatEventResponse;
import test.inquirybot.service.liveperson.model.response.GetChatEventResponse;
import test.inquirybot.service.liveperson.model.response.GetChatRequestResponse;
import test.inquirybot.service.liveperson.model.response.GetDomainResponse;
import test.inquirybot.service.liveperson.model.response.LoginResponse;
import test.inquirybot.service.liveperson.model.response.RetrieveAgentInfoResponse;
import test.inquirybot.service.liveperson.model.response.StartAgentSessionResponse;

/**
 * LivePerson API client
 * 
 * @author Minkeat.Wong
 *
 */
@Slf4j
public class LivePersonApiClient {
	
	private RestTemplate restTemplate;
	private String account;
	private String apiUrl;
	
	private MultiValueMap<String, String> defaultPostHeader = new HttpHeaders();
	
	/** URL constant **/
	private static final String BEARER_STRING  ="Bearer %s";
	private static final String SESSION_ID_COOKIE = "session_id";
	
	/** Domain API URL **/
	private static final String GET_DOMAIN_URL = "%s/account/%s/service/%s/baseURI.json?version=1.0";

	/** Login API URL **/
	private static final String POST_LOGIN_URL 			= "https://%s/api/account/%s/login?v=1.3";
	private static final String POST_LOGIN_REFRESH_URL 	= "https://%s/api/account/%s/refresh";

	public static final String LOGIN_SESSION_ID = "LOGIN_SESSION_ID";
	public static final String LOGIN_BEARER 	= "LOGIN_BEARER";
	public static final String LOGIN_CSRF 		= "CSRF";

	/** Chat Agent API URL **/
	private static final String AGENT_SESSION_BASE_URL 		 = "https://%s/api/account/%s/agentSession";
	
	private static final String POST_START_AGENT_SESSION_URL = AGENT_SESSION_BASE_URL+"?v=1&NC=true";
	private static final String AGENT_SESSION_KEY = "agentSession/";
	
	private static final String CHAT_AGENT_API_BASE_URL 	 = AGENT_SESSION_BASE_URL+"/%s";
	private static final String GET_RETRIEVE_AGENT_INFO_URL	 = CHAT_AGENT_API_BASE_URL+"/info?v=1&NC=true";

	public static final String AGENT_INFO_MAX_CHAT = "MAX_CHAT";
	
	private static final String GET_CHAT_REQUEST_URL	 	 = CHAT_AGENT_API_BASE_URL+"/incomingRequests?v=1&NC=true";
	private static final String POST_ACCEPT_CHAT_REQUEST_URL = CHAT_AGENT_API_BASE_URL+"/incomingRequests?v=1&NC=true";
	private static final String GET_CHAT_EVENTS_URL			 = CHAT_AGENT_API_BASE_URL+"/chat/%s/events?v=1&NC=true";
	//same url as get chat event
	private static final String POST_ADD_CHAT_EVENT_URL      = GET_CHAT_EVENTS_URL;
	
	public static final String PENDING_CHAT_REQUEST = "PENDING_CHAT_REQUEST";
	
	private static final String CHAT_SESSION_KEY = "chat/";
	private static final String CHAT_EVENT_KEY 	 = "events/";
	

	/**
	 * 
	 * @param restTemplate
	 * @param livePersonApiUrl
	 * @param livePersonAccount
	 * @param livePersonUsername
	 * @param livePersonPassword
	 */
	public LivePersonApiClient(RestTemplate restTemplate, 
							   String livePersonApiUrl, 
							   String livePersonAccount){

		this.restTemplate = restTemplate;
		
		this.account  = livePersonAccount;
		this.apiUrl	  = livePersonApiUrl;
		
		//default header
		this.defaultPostHeader.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		this.defaultPostHeader.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
	}
	
	/**
	 * 
	 * @param service
	 * @return
	 * @throws LivePersonApiClientException
	 */
	public String getServiceApiDomain(String service) throws LivePersonApiClientException{
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
		
		HttpEntity<?> requestEntity = new HttpEntity<>(headers);
		
		try{
			ResponseEntity<GetDomainResponse> responseEntity = restTemplate.exchange(String.format(GET_DOMAIN_URL, apiUrl, account,service), 
							  													     HttpMethod.GET, 
							  													     requestEntity, 
							  													     GetDomainResponse.class);
		
			log.info("get domain status : {}",responseEntity.getStatusCode());

			GetDomainResponse response = responseEntity.getBody();
		
			String baseUrl = response.getBaseURI();
			log.info("base URL : {}",baseUrl);
		
			return baseUrl;
		}
		catch(Exception e){
			log.error("Failed get Service Api Domain for {}", service, e);
			throw new LivePersonApiClientException("Failed get Service Api Domain for "+service, e);
		}
	}
	
	/**
	 * 
	 * @param baseUrl
	 * @param userName
	 * @param password
	 * @return
	 * @throws LivePersonApiClientException
	 */
	public Map<String, String> login(String baseUrl, String userName, String password) throws LivePersonApiClientException{
		Map<String, String> resultMap = new HashMap<>();

		try{
			//1. construct default header
			MultiValueMap<String, String> headers = getDefaultHeaders();
			
			//2. prepare request body
			LoginRequest login = new LoginRequest();
			login.setUsername(userName);
			login.setPassword(password);
			
			HttpEntity<LoginRequest> requestEntity = new HttpEntity<>(login, headers);
			
			//3. invoke REST API
			ResponseEntity<LoginResponse> responseEntity = restTemplate.exchange(String.format(POST_LOGIN_URL, baseUrl, account), 
						 														 HttpMethod.POST, 
						 														 requestEntity, 
						 														 LoginResponse.class);
			
			log.info("login status : {}",responseEntity.getStatusCode());
	
			//4. extract session id from response cookie
			String sessionId = getSessionIdFromCookie(responseEntity.getHeaders());
			log.info("session id: {}", sessionId);
			
			resultMap.put(LOGIN_SESSION_ID, sessionId);
	
			//5. extract response body attribute
			LoginResponse response = responseEntity.getBody();
			
			String bearer = response.getBearer();
			String csrf = response.getCsrf();
	
			log.info("bearer : {}, csrf : {}",bearer, csrf);
	
			resultMap.put(LOGIN_BEARER, bearer);
			resultMap.put(LOGIN_CSRF, csrf);
		}
		catch(Exception e){
			log.error("Failed login for {}", userName, e);
			throw new LivePersonApiClientException("Failed login for "+userName, e);
		}
		
		return resultMap;
	}
	
	/**
	 * 
	 * @param sessionId
	 * @param csrf
	 * @param baseUri
	 * @throws LivePersonApiClientException
	 */
	public void refreshLoginSession(String sessionId, String csrf, String baseUri) throws LivePersonApiClientException{
		MultiValueMap<String, String> headers = getDefaultHeaders();
		//added the session_id cookie
		headers.add(HttpHeaders.COOKIE, sessionId);
		
		RefreshLoginSessionRequest refreshRequest = new RefreshLoginSessionRequest();
		refreshRequest.setCsrf(csrf);
		
		HttpEntity<RefreshLoginSessionRequest> requestEntity = new HttpEntity<>(refreshRequest, headers);
		
		
		try{
			ResponseEntity<LoginResponse> responseEntity = restTemplate.exchange(String.format(POST_LOGIN_REFRESH_URL, baseUri, this.account), 
																		  		 HttpMethod.POST, 
																		  		 requestEntity, 
																		  		 LoginResponse.class);
			
			log.info("login refresh {} status : {}",String.format(POST_LOGIN_REFRESH_URL, baseUri, this.account), responseEntity.getStatusCode());
		}
		catch(Exception e){
			log.error("Failed refreshing login session for id {} and csrf {} ", sessionId, csrf);
			throw new LivePersonApiClientException("Failed refreshing login session for "+sessionId+" and csrf "+csrf,e);
		}
	}
	
	/**
	 * 
	 * @param bearer
	 * @param baseUri
	 * @return
	 * @throws LivePersonApiClientException
	 */
	public String startAgentSession(String bearer, String baseUri) throws LivePersonApiClientException{
		String agentSessionId;
		
		//1. construct authorized header
		MultiValueMap<String, String> headers = getAuthorizedHeaders(bearer);
		
		//2. construct request body
		StartAgentSessionRequest requestBody = new StartAgentSessionRequest();
		requestBody.setLoginData("");
		
		HttpEntity<StartAgentSessionRequest> requestEntity = new HttpEntity<>(requestBody, headers);
		
		//3. invoke REST API
		ResponseEntity<StartAgentSessionResponse> responseEntity; 
		
		try{
			responseEntity = restTemplate.exchange(String.format(POST_START_AGENT_SESSION_URL, baseUri, account), 
							 					   HttpMethod.POST, 
							 					   requestEntity, 
							 					   StartAgentSessionResponse.class);
		}
		catch(RestClientException rce){
			log.error("start Agent Session failed", rce); 
			throw new LivePersonApiClientException("Start agent session failed", rce);
		}
		
		log.info("start Agent Session status : {}",responseEntity.getStatusCode());
		
		if(!HttpStatus.CREATED.equals(responseEntity.getStatusCode())){
			throw new LivePersonApiClientException("Start agent session failed with "+responseEntity.getStatusCode());
		}
		
		//4. extract response body attributes
		StartAgentSessionResponse response = responseEntity.getBody();
		
		try{
			String href = response.getAgentSessionLocation().getLink().getHref();
			
			int idx = href.lastIndexOf(AGENT_SESSION_KEY);
			
			if(idx < 0){
				throw new LivePersonApiClientException("Href ["+href+"] invalid ");
			}
			
			agentSessionId = href.substring(idx+AGENT_SESSION_KEY.length());
		}
		catch(NullPointerException e){
			throw new LivePersonApiClientException("Start agent session failed", e);
		}
		
		return agentSessionId;
	}
	
	/**
	 * 
	 * @param bearer
	 * @param agentSessionId
	 * @param baseUri
	 * @return
	 * @throws LivePersonApiClientException
	 */
	public Map<String, String> getAgentInfo(String bearer, 
											String agentSessionId, 
											String baseUri) 
											throws LivePersonApiClientException{

		Map<String, String> resultMap = new HashMap<>();
		
		//1. construct authorized header
		MultiValueMap<String, String> headers = getAuthorizedHeaders(bearer);
		
		//2. construct request entity with headers
		HttpEntity<?> requestEntity = new HttpEntity<>(headers);
		
		//3. invoke API
		ResponseEntity<RetrieveAgentInfoResponse> responseEntity = restTemplate.exchange(String.format(GET_RETRIEVE_AGENT_INFO_URL,baseUri, this.account, agentSessionId),
																						 HttpMethod.GET,
																						 requestEntity,
																						 RetrieveAgentInfoResponse.class);
		
		log.info("retrieve agent response status : {}",responseEntity.getStatusCode());
		
		if(!HttpStatus.OK.equals(responseEntity.getStatusCode())){
			throw new LivePersonApiClientException("retrieve agent info failed with "+responseEntity.getStatusCode());
		}
		
		RetrieveAgentInfoResponse response = responseEntity.getBody();
		
		try{
			String maxChatStr = response.getInfo().getMaxChats();
			log.info("max chat for agent : {} ", maxChatStr);
			resultMap.put(AGENT_INFO_MAX_CHAT, maxChatStr);

		}
		catch(NullPointerException | NumberFormatException e ){
			throw new LivePersonApiClientException("Failed retrieving max chats for agent",e);
		}
		
		return resultMap;
	}
	
	/**
	 * 
	 * @param bearer
	 * @param agentSessionId
	 * @param baseUri
	 * @return
	 * @throws LivePersonApiClientException
	 */
	public Map<String, String> getPendingChatRequest(String bearer, 
													 String agentSessionId, 
													 String baseUri) 
													throws LivePersonApiClientException{
		Map<String, String> resultMap = new HashMap<>();
		
		//1. construct authorized header
		MultiValueMap<String, String> headers = getAuthorizedHeaders(bearer);
		
		//2. construct request entity with headers
		HttpEntity<?> requestEntity = new HttpEntity<>(headers);
		
		//3. invoke API
		ResponseEntity<GetChatRequestResponse> responseEntity;
		
		try{
			responseEntity= restTemplate.exchange(String.format(GET_CHAT_REQUEST_URL, baseUri, this.account, agentSessionId),
												  HttpMethod.GET,
												  requestEntity,
												  GetChatRequestResponse.class);
		}
		catch(RestClientException rce){
			log.error("Get pending chat request API Error", rce);
			throw new LivePersonApiClientException(rce);
		}
		
		log.info("get chat request response status : {}",responseEntity.getStatusCode());
		
		if(!HttpStatus.OK.equals(responseEntity.getStatusCode())){
			throw new LivePersonApiClientException("get chat request failed with "+responseEntity.getStatusCode());
		}
		
		try{
			String pendingChatRequest = responseEntity.getBody().getIncomingRequests().getRingingCount();
			
			log.info("Pending chat request : {}", pendingChatRequest);
			resultMap.put(PENDING_CHAT_REQUEST, pendingChatRequest);
		}
		catch(NullPointerException e){
			log.error("response body may be invalid",e);
			throw new LivePersonApiClientException(e);
		}
		
		return resultMap;
	}
	
	/**
	 * 
	 * @param bearer
	 * @param agentSessionId
	 * @param baseUri
	 * @return
	 * @throws LivePersonApiClientException
	 */
	public String acceptNextChatRequest(String bearer, 
			 							String agentSessionId, 
			 							String baseUri)
									   throws LivePersonApiClientException{
		String chatId = null;
		
		//1. construct authorized header
		MultiValueMap<String, String> headers = getAuthorizedHeaders(bearer);
				
		//2. construct request entity with headers
		AcceptChatRequest request = new AcceptChatRequest();
		HttpEntity<AcceptChatRequest> requestEntity = new HttpEntity<>(request, headers);
		
		//3. invoke API
		ResponseEntity<AcceptChatResponse> responseEntity;
		
		try{
			responseEntity= restTemplate.exchange(String.format(POST_ACCEPT_CHAT_REQUEST_URL, baseUri, this.account, agentSessionId),
												  HttpMethod.POST,
												  requestEntity,
												  AcceptChatResponse.class);
		}
		catch(RestClientException rce){
			log.error("accept pending chat request API Error", rce);
			throw new LivePersonApiClientException(rce);
		}
		
		//4. extract response body attributes
		AcceptChatResponse response = responseEntity.getBody();
		
		try{
			String href = response.getChatLocation().getLink().getHref();
			
			int idx = href.lastIndexOf(CHAT_SESSION_KEY);
			
			if(idx < 0){
				throw new LivePersonApiClientException("Href ["+href+"] invalid ");
			}
			
			chatId = href.substring(idx+CHAT_SESSION_KEY.length());
		}
		catch(NullPointerException e){
			throw new LivePersonApiClientException("accept chat failed", e);
		}
		
		return chatId;
	}

	/**
	 * adding line is to add a chat event of type ="line" and textType and text attribute set
	 * @param bearer
	 * @param agentSessionId
	 * @param baseUri
	 * @param chatId
	 * @param textMsg
	 * @return
	 * @throws LivePersonApiClientException
	 */
	public String addTextLine(String bearer, 
			  			      String agentSessionId, 
			  			      String baseUri,
			  			      String chatId,
			  			      String textMsg)
				throws LivePersonApiClientException{
		String eventId = null;
		
		Event event = new Event();
		//TODO: use enum
		event.setType("line");
		event.setTextType("plain");
		event.setText(textMsg);
		
		eventId = addChatEvent(bearer, agentSessionId, baseUri, chatId, event);
		
		log.info("add line event id : {}", eventId);
		
		return eventId;
	}
	/**
	 * Ending a chat is by adding an event "state=ended"
	 * @param bearer
	 * @param agentSessionId
	 * @param baseUri
	 * @param chatId
	 * @return
	 * @throws LivePersonApiClientException
	 */
	public String endChat(String bearer, 
			 			  String agentSessionId, 
			 			  String baseUri,
			 			  String chatId)
					throws LivePersonApiClientException{
		String eventId = null;
		
		Event event = new Event();
		//TODO: use enum
		event.setType("state");
		event.setState("ended");
		
		eventId = addChatEvent(bearer, agentSessionId, baseUri, chatId, event);
		
		log.info("end chat event id : {}", eventId);
		
		return eventId;
	}
	
	/**
	 * 
	 * @param bearer
	 * @param agentSessionId
	 * @param baseUri
	 * @param chatId
	 * @param event
	 * @return
	 * @throws LivePersonApiClientException
	 */
	public String addChatEvent(String bearer, 
			  				   String agentSessionId, 
			  				   String baseUri,
			  				   String chatId,
			  				   Event event)
		throws LivePersonApiClientException{
		
		String eventId = null;
		
		//1. construct authorized header
		MultiValueMap<String, String> headers = getAuthorizedHeaders(bearer);
				
		//2. construct request entity with headers
		AddChatEventRequest request = new AddChatEventRequest();
		request.setEvent(event);
		
		HttpEntity<AddChatEventRequest> requestEntity = new HttpEntity<>(request, headers);
		
		//3. invoke API
		ResponseEntity<AddChatEventResponse> responseEntity;
		
		try{
			responseEntity= restTemplate.exchange(String.format(POST_ADD_CHAT_EVENT_URL, baseUri, this.account, agentSessionId, chatId),
												  HttpMethod.POST,
												  requestEntity,
												  AddChatEventResponse.class);
		}
		catch(RestClientException rce){
			log.error("add chat event request API Error", rce);
			throw new LivePersonApiClientException(rce);
		}
		
		//4. extract response body attributes
		AddChatEventResponse response = responseEntity.getBody();
		
		try{
			String href = response.getChatEventLocation().getLink().getHref();
			
			int idx = href.lastIndexOf(CHAT_EVENT_KEY);
			
			if(idx < 0){
				throw new LivePersonApiClientException("Href ["+href+"] invalid ");
			}
			
			eventId = href.substring(idx+CHAT_EVENT_KEY.length());
		}
		catch(NullPointerException e){
			throw new LivePersonApiClientException("invalid add chat event response", e);
		}
		
		return eventId;
	}

	/**
	 * 
	 * @param bearer
	 * @param agentSessionId
	 * @param baseUri
	 * @param chatId
	 * @param fromEventId
	 * @return
	 * @throws LivePersonApiClientException
	 */
	public List<Event> getChatEvents(String bearer, 
			  						 String agentSessionId, 
			  						 String baseUri,
			  						 String chatId, 
			  						 int fromEventId)
			  								 throws LivePersonApiClientException{
		
		List<Event> events = new ArrayList<>();
		
		//1. construct authorized header
		MultiValueMap<String, String> headers = getAuthorizedHeaders(bearer);

		//2. construct request entity with headers
		HttpEntity<AddChatEventRequest> requestEntity = new HttpEntity<>(headers);

		//3. invoke API
		ResponseEntity<GetChatEventResponse> responseEntity;
		
		String url = String.format(GET_CHAT_EVENTS_URL, baseUri, this.account, agentSessionId, chatId) + (fromEventId==0? "":"&from="+fromEventId);
		
		log.info("api url : {}", url);

		try{
			responseEntity= restTemplate.exchange(url,
												  HttpMethod.GET,
												  requestEntity,
												  GetChatEventResponse.class);
		}
		catch(RestClientException rce){
			log.error("get chat event request API Error", rce);
			throw new LivePersonApiClientException(rce);
		}
		
		//4. extract response body attributes
		GetChatEventResponse response = responseEntity.getBody();
		
		try{
			events = response.getEvents().getEvents();
		}
		catch(NullPointerException e){
			throw new LivePersonApiClientException("Invalid get chat event body", e);
		}
		
		return events;
	}
	
	
	/************************** private helper methods ********************/
	
	/**
	 * wrapped the default post header
	 * @return
	 */
	private MultiValueMap<String, String> getDefaultHeaders(){
		HttpHeaders headers = new HttpHeaders();
		headers.putAll(defaultPostHeader);
		
		return headers;
	}
	
	/**
	 * wrapped the default header + bearer token header
	 * @return
	 * @throws LivePersonApiClientException
	 */
	private MultiValueMap<String, String> getAuthorizedHeaders(String bearer) throws LivePersonApiClientException{
		if(bearer == null){
			throw new LivePersonApiClientException("no bearer");
		}
			
		HttpHeaders headers = new HttpHeaders();
		headers.putAll(defaultPostHeader);
		
		headers.add(HttpHeaders.AUTHORIZATION, String.format(BEARER_STRING, bearer));
		
		return headers;
	}
	
	/**
	 * extract the session_id cookie from response header "Set-Cookie"
	 * @param httpHeaders
	 * @return
	 */
	private String getSessionIdFromCookie(HttpHeaders httpHeaders){
		
		List<String> cookies = httpHeaders.get(HttpHeaders.SET_COOKIE);
		String sessionID = null;
		
		for(String cookie: cookies){
			if(cookie!=null && cookie.contains(SESSION_ID_COOKIE)){
				int startIdx = cookie.indexOf(SESSION_ID_COOKIE);
				int endIdx = cookie.indexOf(";", startIdx);
				
				if(endIdx<0){
					endIdx = cookie.length();
				}
				sessionID = cookie.substring(startIdx, endIdx);
			}
		}
		
		return sessionID;
	}
}
