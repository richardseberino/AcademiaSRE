package com.gm4c.tef.healthcheck;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Qualifier("tefhealthcustom")
@Component
public class TEFHealthIndicator extends AbstractHealthIndicator {

	protected void doHealthCheck(Health.Builder builder) throws Exception {

		long start = System.currentTimeMillis();
		long finish;
		long timeElapsed;
		
		finish = System.currentTimeMillis();
		timeElapsed = finish - start;

		builder.up().withDetail("TEFHealthIndicator - elapsedTime (ms)", timeElapsed);
//		builder.down().withDetail("TEFHealthIndicator - elapsedTime (ms)", timeElapsed);
		return;
	}


}