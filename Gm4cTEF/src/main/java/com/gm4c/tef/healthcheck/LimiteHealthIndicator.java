package com.gm4c.tef.healthcheck;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@ConditionalOnProperty(name = "management.health.limite.enabled", havingValue = "true", matchIfMissing = true)
public class LimiteHealthIndicator extends AbstractHealthIndicator {

	@Value("${management.health.limite.enabled}")
	private Boolean hcEnabled;

	@Value("${management.health.limite.level}")
	private long hcLevel;

	@Value("${management.health.limite.url}")
	private String hcUrl;
		
	@Value("${management.health.limite.threshold:500}")
	private Integer hcThreshold;
	
	protected void doHealthCheck(Health.Builder builder) throws Exception {

		if ( hcEnabled == true ) {
			long start = System.currentTimeMillis();
			Date  date = new Date();
			Timestamp startTimestamp = new Timestamp(date.getTime());
	
			try {
	
					String url =  hcUrl + "/actuator/health";
					RestTemplate restTemplate = new RestTemplate();
					ResponseEntity<String> response
					  = restTemplate.getForEntity(url, String.class);
					assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
	
					long finish = System.currentTimeMillis();
					date = new Date();
					Timestamp endTimestamp = new Timestamp(date.getTime());
					long timeElapsed = finish - start;
				
					builder.up().withDetail("healthCheckLevel", hcLevel);
					builder.up().withDetail("status", "PASS");
					builder.up().withDetail("product", "Limite");
					builder.up().withDetail("Timestamp Start", startTimestamp.toString());
					builder.up().withDetail("Timestamp End", endTimestamp.toString());
					builder.up().withDetail("elapsedTime (ms)", timeElapsed);
					builder.up().withDetail("threshold (ms)", hcThreshold);
					builder.up().withDetail("payload", response);
	
				}
		
				catch (Exception ex) {
					long finish = System.currentTimeMillis();
					date = new Date();
					Timestamp endTimestamp = new Timestamp(date.getTime());
			
					long timeElapsed = finish - start;
					builder.down().withDetail("healthCheckLevel", hcLevel);
					builder.down().withDetail("status", "NOT PASS");
					builder.down().withDetail("product", "Limite");
					builder.down().withDetail("startTimestamp", startTimestamp);
					builder.down().withDetail("endTimestamp", endTimestamp);
					builder.down().withDetail("elapsedTime (ms)", timeElapsed); 
					builder.down().withDetail("threshold (ms)", hcThreshold);
					builder.down().withDetail("message", ex.getLocalizedMessage()); 
				}
				
				return;
		}
	}
}