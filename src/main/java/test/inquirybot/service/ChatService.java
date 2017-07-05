package test.inquirybot.service;

import test.inquirybot.service.exception.ChatServiceException;
import test.inquirybot.service.restclient.LivePersonApiClient;

public interface ChatService {
	
	/**
	 * 
	 * @param apiClient
	 * @param bearer
	 * @param agentSessionId
	 * @param baseUri
	 * @throws ChatServiceException
	 */
	void startChat(LivePersonApiClient apiClient, 
				   String bearer, 
				   String agentSessionId, 
				   String baseUri) throws ChatServiceException;
}
