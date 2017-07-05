package test.inquirybot.service.liveperson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import test.inquirybot.service.ChatService;
import test.inquirybot.service.exception.ChatServiceException;
import test.inquirybot.service.exception.LivePersonApiClientException;
import test.inquirybot.service.liveperson.model.Event;
import test.inquirybot.service.restclient.LivePersonApiClient;

@Slf4j
public class LivePersonChatServiceImpl implements ChatService{

	@Override
	public void startChat(LivePersonApiClient apiClient, String bearer, String agentSessionId, String baseUri)
			throws ChatServiceException {
		log.info("Accepting a chat..");
		int refEventId = 0;
		String chatId = null;

		try {
			int fromEventId = 0;
			Map<String, Object> chatSessionMap = new HashMap<>();

			//1. accept chat
			chatId = apiClient.acceptNextChatRequest(bearer, agentSessionId, baseUri);
			log.info("Chat session id : {}", chatId);
			
			if(chatId == null){
				throw new ChatServiceException("Invalid chat session id");
			}
			
			//2. conversation flow
			while(refEventId>=0){
				List<Event> chatEvents = apiClient.getChatEvents(bearer, agentSessionId, baseUri, chatId, fromEventId);
				
				refEventId = responseChat(chatEvents, chatSessionMap, apiClient, bearer, agentSessionId, baseUri, chatId, refEventId);
				log.info("Event id : {}", refEventId);
				
				if(refEventId>=0){
					Thread.sleep(500L);
					//once the gap between max no. of event and last processed visitor message exceed threshold
					if(chatEvents.size() - refEventId > 5){
						//move the event start block up 5 entries, to ensure the event return always > 1 so that json attribute always converted to array
						fromEventId = fromEventId + 5;
					}
				}

			}
			//TODO:
			
			//: end chat
			String eventId = apiClient.endChat(bearer, agentSessionId, baseUri, chatId);
			log.info("Chat last event id : {}", eventId);
			chatSessionMap.clear();
		} 
		catch (LivePersonApiClientException | InterruptedException e) {
			
			if(refEventId>0 && chatId !=null){
				try {
					apiClient.endChat(bearer, agentSessionId, baseUri, chatId);
				} 
				catch (LivePersonApiClientException e1) {
					log.error("failed to end chat for error case ", e1);
				}
			}
			
			log.error("Error in accepting a chat session",e);
			throw new ChatServiceException("Error in accepting a chat session",e);
		}
		finally{
			
		}
	}
	
	private int responseChat(List<Event> chatEvent, 
								 Map<String, Object> chatSessionMap,
								 LivePersonApiClient apiClient,
								 String bearer, 
								 String agentSessionId, 
								 String baseUri,
								 String chatId,
								 int currentChatEventId) throws LivePersonApiClientException{
		
		int refVisitorMessageEventId;
		//look for visitor message from next event 
		Event nextChatMessageEvent = getVisitorNextChatMessageEvent(chatEvent, currentChatEventId+1);
		
		//if found visitor chat message event
		if(nextChatMessageEvent!=null){
			String chatMsg = nextChatMessageEvent.getText();
			refVisitorMessageEventId = Integer.valueOf(nextChatMessageEvent.getId());

			if(chatMsg!=null){
				String nextEventId = null;
				if(chatMsg.toLowerCase().contains("hi")){
					nextEventId = apiClient.addTextLine(bearer, agentSessionId, baseUri, chatId, "Hello, how can i help?");
					
				}
				else if(chatMsg.toLowerCase().contains("bye")){
					apiClient.addTextLine(bearer, agentSessionId, baseUri, chatId, "Bye!");
					
					nextEventId="-1"; //set condition to end
				}
				//if already reply, event id will be incremented, update the ref event id
				if(nextEventId!=null){
					refVisitorMessageEventId = Integer.valueOf(nextEventId);
				}
			}
		}
		//no visitor message
		else{
			//move visitor message event ref to last event
			refVisitorMessageEventId = Integer.valueOf(chatEvent.get(chatEvent.size()-1).getId());
		}
		
		
		return refVisitorMessageEventId;
	}

	/**
	 * 
	 * @param chatEvents
	 * @return
	 */
	private Event getVisitorNextChatMessageEvent(List<Event> chatEvents, int eventId){

		boolean found = false;

		Event event = null;

		if(chatEvents!=null && !chatEvents.isEmpty()){
			for(int idx = eventId; idx<chatEvents.size(); idx++ ){
				event = chatEvents.get(idx);
				
				//TODO: replace literal by constant/enum
				if("visitor".equals(event.getSource()) &&
					//for now only handle plain text type
				   "plain".equals(event.getTextType())){
					
					found = true;
					break;
				}
			}
		}
		//set to null if not found
		if(!found){
			event = null;
		}
		
		return event;
	}
}
