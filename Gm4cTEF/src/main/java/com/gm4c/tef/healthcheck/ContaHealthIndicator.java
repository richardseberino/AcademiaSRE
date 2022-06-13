package com.gm4c.tef.healthcheck;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


/**
 * Simple implementation of a {@link HealthIndicator} returning status information for
 * Cassandra data stores.
 *
 * @author Julien Dubois
 * @since 2.0.0
 * https://github.com/spring-projects/spring-boot/blob/v2.1.3.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/cassandra/CassandraHealthIndicator.java
 */

@Component
//@ConditionalOnProperty(name = "management.health.senha.enabled", havingValue = "true", matchIfMissing = true)
public class ContaHealthIndicator extends AbstractHealthIndicator {

	
	@Value("${management.health.conta.enabled}")
	private Boolean hcEnabled;

	@Value("${management.health.conta.level}")
	private long hcLevel;

	@Value("${management.health.conta.url}")
	private String hcUrl;
		
	@Value("${management.health.conta.threshold:500}")
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
					builder.up().withDetail("product", "Conta");
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
					builder.down().withDetail("product", "Conta");
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