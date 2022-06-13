package com.gm4c.tef.healthcheck;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class SampleHealthIndicator implements HealthIndicator {
	@Override
	public Health health(){
//		int errorCode = 401;
//		int errorCode = check();	
//		if (errorCode != 0 ) {
//			return Health.down().withDetail("Error Code", errorCode).build();
//		}
		return Health.up().withDetail("SampleHealthIndicator", 401).build();
//		return Health.up().build();
//		return Health.down().withDetail("Sample Error Code", 401).build();
	}
}
