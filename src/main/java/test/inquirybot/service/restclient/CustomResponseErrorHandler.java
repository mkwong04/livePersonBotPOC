package test.inquirybot.service.restclient;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * as Spring rest template default handling of error (4xx/5xx) is to throw RestClientException, 
 * this class is to override the default error response by consume and log the details
 *  
 * @author Minkeat.Wong
 *
 */
@Slf4j
public class CustomResponseErrorHandler implements ResponseErrorHandler{

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		HttpStatus.Series statusSeries= response.getStatusCode().series();
		
		//if 4xx or 5xx series, threat as error case
		return (HttpStatus.Series.CLIENT_ERROR.equals(statusSeries) || 
				HttpStatus.Series.SERVER_ERROR.equals(statusSeries) );
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		
		log.warn("Rest Client call error : {} {}",response.getStatusCode(), response.getStatusText());
		
	}
}
