package test.inquirybot.service.exception;

/**
 * Chat Service exception
 * @author minkeat.wong
 *
 */
public class LivePersonApiClientException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public LivePersonApiClientException(){
		super();
	}
	
	/**
	 * 
	 * @param msg
	 */
	public LivePersonApiClientException(String msg){
		super(msg);
	}
	
	/**
	 * 
	 * @param t
	 */
	public LivePersonApiClientException(Throwable t){
		super(t);
	}

	/**
	 * 
	 * @param msg
	 * @param t
	 */
	public LivePersonApiClientException(String msg, Throwable t){
		super(msg, t);
	}
}
