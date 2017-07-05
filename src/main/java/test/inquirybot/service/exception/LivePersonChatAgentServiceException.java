package test.inquirybot.service.exception;

/**
 *Live Person Chat Agent Service exception
 * @author minkeat.wong
 *
 */
public class LivePersonChatAgentServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public LivePersonChatAgentServiceException(){
		super();
	}
	
	/**
	 * 
	 * @param msg
	 */
	public LivePersonChatAgentServiceException(String msg){
		super(msg);
	}
	
	/**
	 * 
	 * @param t
	 */
	public LivePersonChatAgentServiceException(Throwable t){
		super(t);
	}

	/**
	 * 
	 * @param msg
	 * @param t
	 */
	public LivePersonChatAgentServiceException(String msg, Throwable t){
		super(msg, t);
	}
}
