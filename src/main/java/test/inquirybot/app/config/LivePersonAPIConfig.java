package test.inquirybot.app.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import test.inquirybot.service.AgentService;
import test.inquirybot.service.ChatService;
import test.inquirybot.service.exception.ChatServiceException;
import test.inquirybot.service.exception.LivePersonChatAgentServiceException;
import test.inquirybot.service.liveperson.LivePersonChatAgentServiceImpl;
import test.inquirybot.service.liveperson.LivePersonChatServiceImpl;
import test.inquirybot.service.restclient.CustomResponseErrorHandler;
import test.inquirybot.service.restclient.LivePersonApiClient;

@Slf4j
@Configuration
@PropertySource("classpath:liveperson.properties")
public class LivePersonAPIConfig {

	@Value("${liveperson.api.url}")
	private String apiUrl;
	
	@Value("${liveperson.api.account}")
	private String account;
	
	@Value("${liveperson.api.username}")
	private String userName;
	
	@Value("${liveperson.api.password}")
	private String password;
	
	@Value("${bot.pulling.interval}")
	private String botPullingInterval;
	
	@Bean
	public long getBotPullingInterval(){
		
		if(botPullingInterval == null ){
			//return defaut 1 min
			return 1 * 60 * 1000L;
		}
		else{
			return Long.valueOf(botPullingInterval);
		}
	}
	
	@Bean
	public String getLivePersonUserName(){
		//support override by -D system property
		return System.getProperty("liveperson.api.username", userName);
	}
	
	@Bean
	public String getLivePersonUserPassword(){
		//support override by -D system property
		return System.getProperty("liveperson.api.password", password);
	}

	@Bean
	public RestTemplate livePersonRestTemplate(){

		List<HttpMessageConverter<?>> messageConverters = Arrays.asList(new HttpMessageConverter[] {
															new MappingJackson2HttpMessageConverter()
														  });

		RestTemplate restTemplate = new RestTemplate();
		//configure message converter
		restTemplate.setMessageConverters(messageConverters);
		//use apache http component client
		HttpComponentsClientHttpRequestFactory requestFac = new HttpComponentsClientHttpRequestFactory();
		requestFac.setConnectTimeout(60*1000);
		requestFac.setReadTimeout(60*1000);
		restTemplate.setRequestFactory(requestFac);
		
		restTemplate.setErrorHandler(new CustomResponseErrorHandler());
		
		return restTemplate;
	}

	/**
	 * 
	 * @return
	 */
	@Bean
	public LivePersonApiClient livePersonApiClient(){
		log.info("initializing live person API client ...");

		return new LivePersonApiClient(livePersonRestTemplate(),
									   apiUrl,
									   account);
	}
	@Bean 
	public ChatService inquiryChatService() throws ChatServiceException{
		log.info("initializing live person chat service ...");
		return new LivePersonChatServiceImpl();
	}
	
	@Bean
	public AgentService livePersonChatAgentService() throws LivePersonChatAgentServiceException{
		log.info("initializing live person chat agent service ...");
		try {
			LivePersonChatAgentServiceImpl livePersonChatAgent = new LivePersonChatAgentServiceImpl(livePersonApiClient(),
																									inquiryChatService(),
																									getLivePersonUserName(),
																									getLivePersonUserPassword());
			//obtain base URL base on service domain
			livePersonChatAgent.initDomain();
			
			livePersonChatAgent.startChatAgentSession("test", getBotPullingInterval());
			
			return livePersonChatAgent;
		} catch (ChatServiceException e) {
			throw new LivePersonChatAgentServiceException(e);
		}
		
		
	}
	
}
