package test.inquirybot.app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.extern.slf4j.Slf4j;
import test.inquirybot.app.config.LivePersonAPIConfig;

/**
 * Spring boot application for inquiry bot
 * @author Minkeat.Wong
 *
 */
@SpringBootApplication(scanBasePackages = {"test.inquirybot"})
@Slf4j
public class InquiryBotApp {
	public static final void main(String[] args) throws Exception{
		log.info("Starting standalone spring boot application");

		ConfigurableApplicationContext context=null;
		
		try{
			context = new SpringApplicationBuilder(InquiryBotApp.class).sources(LivePersonAPIConfig.class)
												     				   .run(args);
		}
		finally{
		
		}

	}
}
