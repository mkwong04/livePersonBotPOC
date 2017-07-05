package test.inquirybot.service.exception;

/**
 * Chat Service exception
 * @author minkeat.wong
 *
 */
public class ChatServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public ChatServiceException(){
		super();
	}
	
	/**
	 * 
	 * @param msg
	 */
	public ChatServiceException(String msg){
		super(msg);
	}
	
	/**
	 * 
	 * @param t
	 */
	public ChatServiceException(Throwable t){
		super(t);
	}

	/**
	 * 
	 * @param msg
	 * @param t
	 */
	public ChatServiceException(String msg, Throwable t){
		super(msg, t);
	}
}
