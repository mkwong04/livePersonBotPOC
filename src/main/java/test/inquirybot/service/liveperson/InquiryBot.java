package test.inquirybot.service.liveperson;

import java.util.Map;

import org.slf4j.Logger;

import test.inquirybot.service.ChatService;
import test.inquirybot.service.exception.ChatServiceException;
import test.inquirybot.service.exception.LivePersonApiClientException;
import test.inquirybot.service.restclient.LivePersonApiClient;

public class InquiryBot implements Runnable{
	
	private LivePersonApiClient apiClient;
	private String bearer;
	private String agentSessionId;
	private String baseUri;
	private ChatService chatService;
	private long sleepInterval;
	private boolean endRun = false;
	private Logger logger;
	
	public InquiryBot(LivePersonApiClient apiClient,
					  String bearer,
					  String agentSessionId,
					  String baseUri,
					  ChatService chatService, 
					  long sleepInterval, 
					  Logger logger){
		
		this.apiClient 		= apiClient;
		this.bearer 		= bearer;
		this.agentSessionId = agentSessionId;
		this.baseUri 		= baseUri;

		this.chatService = chatService;

		this.sleepInterval = sleepInterval;
		
		this.logger = logger;
	}

	@Override
	public void run() {
		//stop thread is manual shutdown or interrupted
		while(!endRun && !Thread.interrupted()){
			//pull for chat request
			
			if(hasPendingChatRequest()){
				this.logger.info("has chat request");
				try {
					chatService.startChat(this.apiClient, this.bearer, this.agentSessionId, this.baseUri);
				} 
				catch (ChatServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				try {
					this.logger.info("no chat request, sleep for {} ms", sleepInterval);
					Thread.sleep(sleepInterval);
				} 
				catch (InterruptedException e) {
					
				}
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean hasPendingChatRequest(){
		boolean hasPendingChatRequest = false;
		
		try {
			Map<String, String> resultMap = apiClient.getPendingChatRequest(bearer, agentSessionId, baseUri);
			
			String pendingChatRequestStr = resultMap.get(LivePersonApiClient.PENDING_CHAT_REQUEST);
			
			if(pendingChatRequestStr!=null && Integer.valueOf(pendingChatRequestStr)>0){
				hasPendingChatRequest = true;
			}
		} 
		catch (LivePersonApiClientException e) {
			logger.warn("Error in get pending chat request API call", e);
		}
		catch(NumberFormatException nfe){
			logger.warn("Error in getting pending chat request value", nfe);
		}
		
		return hasPendingChatRequest;
	}

}
