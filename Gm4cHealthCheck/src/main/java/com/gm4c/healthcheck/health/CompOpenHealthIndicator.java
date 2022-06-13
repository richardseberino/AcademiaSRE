package com.gm4c.healthcheck.health;


import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;

@Component
public class CompOpenHealthIndicator implements HealthIndicator {

	@Value("${management.gm4c.name:\"gm4c\"}")
	private String hcProject;

	@Value("${server.port:0}")
	private Integer hcPort;

	@Autowired
	MeterRegistry registry;
	
//	@Autowired
//	DistributionSummary summary;

	@Override
	public Health health(){

//		Health.Builder builder = null; 

		long start = System.currentTimeMillis();
		Date  date = new Date();
		Timestamp startTimestamp = new Timestamp(date.getTime());

		final DistributionSummary summary = DistributionSummary
			    .builder("hca.compOpen.sample"+hcProject)
			    .description("Health Check - CompOpen - L3 - Summary") // optional
//			    .baseUnit("bytes") // optional (1)
			    .baseUnit("miliseconds") // optional (1)
			    .tags("region", "test") // optional
//			    .scale(100) // optional (2)
			    .register(registry);
		
		
		long finish = System.currentTimeMillis();
		date = new Date();
		Timestamp endTimestamp = new Timestamp(date.getTime());
		long timeElapsed = finish - start;

		summary.record(timeElapsed);
		Metrics.counter("hca.compOpen.sample").increment();
		
		return Health.up()
//        		.withDetail("CompOpenHealthIndicator", 401)
        		.withDetail("status", "PASS")
				.withDetail("product", hcProject)
				.withDetail("Timestamp Start", startTimestamp.toString())
				.withDetail("Timestamp End", endTimestamp.toString())
				.withDetail("elapsedTime (ms)", timeElapsed)
//				.withDetail("threshold (ms)", hcThreshold)
//				.withDetail("message", "")
        		.withDetail("port", hcPort)
	            .build();
//		return Health.up().withDetail("CompOpenHealthIndicator", 401).build();
//		return Health.up().build();
//		return Health.down().withDetail("Sample Error Code", 401).build();
	}
}
