package com.gm4c.healthcheck.health;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;

@Component
//@ConditionalOnProperty(name = "management.health.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class KafkaHealthIndicatorProducer implements HealthIndicator {

	@Value("${management.health.kafka.enabled:false}")
	private Boolean hcEnabled;

	@Value("${management.gm4c.name:\"gm4c\"}")
	private String hcProject;

	@Value("${management.health.kafka.threshold:1000}")
	private Integer hcThreshold;

	private KafkaTemplate<String, String> kafka;

	@Autowired
	MeterRegistry registry;

	public KafkaHealthIndicatorProducer(KafkaTemplate<String, String> kafka) {
        this.kafka = kafka;
    }

    @Override
    public Health health() {
		if (!hcEnabled) {
            return Health.unknown()
                    .withDetail("status", "NOT CHECKED")
                    .build();
		}

		DistributionSummary summary = null;

		long start = System.currentTimeMillis();
		Date  date = new Date();
		Timestamp startTimestamp = new Timestamp(date.getTime());
		
        try {
        	kafka.send("kafka-health-indicator", "❥").get(100, TimeUnit.MILLISECONDS);

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
    		long finish = System.currentTimeMillis();
    		date = new Date();
    		Timestamp endTimestamp = new Timestamp(date.getTime());

    		long timeElapsed = finish - start;
    		
    		summary = DistributionSummary
    			    .builder("hca.kafka.producer."+hcProject)
    			    .description("Health Check - Kafka - L3 - Summary") 
    			    .baseUnit("miliseconds") 
    			    .tags("region", "test") 
    			    .tags("hcStatus", "UP") 
    			    .tags("status", "NO PASS") 
    			    .tags("message", e.toString()) 
    			    .tags("threshold (ms)", Long.toString(hcThreshold)) 
    			    .register(registry);
    		summary.record(timeElapsed);

        	return Health.up().withDetail("message", e.toString()).build();
//        	return Health.down(e).build();
        }
		long finish = System.currentTimeMillis();
		date = new Date();
		Timestamp endTimestamp = new Timestamp(date.getTime());

		long timeElapsed = finish - start;
		
		summary = DistributionSummary
			    .builder("hca.kafka.producer."+hcProject)
			    .description("Health Check - Kafka - L3 - Summary") 
			    .baseUnit("miliseconds") 
			    .tags("region", "test") 
			    .tags("hcStatus", "UP") 
			    .tags("status", "PASS") 
			    .tags("message", "OK") 
			    .tags("threshold (ms)", Long.toString(hcThreshold)) 
			    .register(registry);
		summary.record(timeElapsed);

        return Health.up().build();
    }
}