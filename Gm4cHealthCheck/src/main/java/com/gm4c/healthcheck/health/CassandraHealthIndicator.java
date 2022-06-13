package com.gm4c.healthcheck.health;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import com.gm4c.healthcheck.dto.CassandraHealthIndicatorDto;
import com.gm4c.healthcheck.repository.CassandraHealthIndicatorRepository;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;

@Component
//@ConditionalOnProperty(name = "management.health.cassandra.enabled", havingValue = "true", matchIfMissing = false)
public class CassandraHealthIndicator extends AbstractHealthIndicator {

	@Value("${management.health.cassandra.enabled:false}")
	private Boolean hcEnabled;
	
	@Value("${management.gm4c.name:\"gm4c\"}")
	private String hcProject;

	@Value("${management.health.cassandra.threshold:1000}")
	private long hcThreshold;

	@Value("${management.health.cassandra.query:'SELECT release_version from system.local'}")
	private String hcQuery;

	@Autowired
	private CassandraHealthIndicatorRepository rep;
	
	@Autowired
	MeterRegistry registry;

	protected void doHealthCheck(Health.Builder builder) throws Exception {

		if (!hcEnabled) {
			builder.unknown().withDetail("status", "NOT CHECKED");
			return;
		}

		DistributionSummary summary = null;
		
		long start = System.currentTimeMillis();
		Date  date = new Date();
		Timestamp startTimestamp = new Timestamp(date.getTime());

		try {
			List<CassandraHealthIndicatorDto> cassandraVersion = rep.findVersion();
			
			CassandraHealthIndicatorDto cassandraReg = cassandraVersion.get(0);
	
			long finish = System.currentTimeMillis();
			date = new Date();
			Timestamp endTimestamp = new Timestamp(date.getTime());
			long timeElapsed = finish - start;
			
			if (cassandraVersion.size()>0) {
				if (timeElapsed <= hcThreshold) {
					builder.up().withDetail("status", "PASS");
					builder.up().withDetail("product", "Cassandra");
					builder.up().withDetail("version", cassandraReg.getRelease_version());
					builder.up().withDetail("Timestamp Start", startTimestamp.toString());
					builder.up().withDetail("Timestamp End", endTimestamp.toString());
					builder.up().withDetail("elapsedTime (ms)", timeElapsed);
					builder.up().withDetail("threshold (ms)", hcThreshold);
					builder.up().withDetail("message", "OK");

					summary = DistributionSummary
						    .builder("hca.cassandra.query."+hcProject)
						    .description("Health Check - Cassandra - L3 - Summary") 
						    .baseUnit("miliseconds") 
						    .tags("region", "test") 
						    .tags("hcStatus", "UP") 
						    .tags("status", "PASS") 
						    .tags("threshold (ms)", Long.toString(hcThreshold)) 
						    .tags("message", "OK") 
						    .register(registry);
					summary.record(timeElapsed);

				}	else {
					builder.up().withDetail("status", "NOT PASS");
					builder.up().withDetail("product", "Cassandra");
					builder.up().withDetail("version", cassandraReg.getRelease_version());
					builder.up().withDetail("startTimestamp", startTimestamp);
					builder.up().withDetail("endTimestamp", endTimestamp);
					builder.up().withDetail("elapsedTime (ms)", timeElapsed); 
					builder.up().withDetail("threshold (ms)", hcThreshold);
					builder.up().withDetail("message", "Threshold"); 

					summary = DistributionSummary
						    .builder("hca.cassandra.query."+hcProject)
						    .description("Health Check - Cassandra - L3 - Summary") 
						    .baseUnit("miliseconds") 
						    .tags("region", "test") 
						    .tags("hcStatus", "UP") 
						    .tags("status", "NOT PASS") 
						    .tags("message", "Threshold") 
						    .tags("threshold (ms)", Long.toString(hcThreshold)) 
						    .register(registry);
					summary.record(timeElapsed);

				}
	
			}	else {
					builder.up().withDetail("status", "NOT PASS");
					builder.up().withDetail("product", "Cassandra");
					builder.up().withDetail("version", cassandraReg.getRelease_version());
					builder.up().withDetail("startTimestamp", startTimestamp);
					builder.up().withDetail("endTimestamp", endTimestamp);
					builder.up().withDetail("elapsedTime (ms)", timeElapsed); 
					builder.up().withDetail("threshold (ms)", hcThreshold);
					builder.up().withDetail("message", "Query"); 

					summary = DistributionSummary
						    .builder("hca.cassandra.query."+hcProject)
						    .description("Health Check - Cassandra - L3 - Summary") 
						    .baseUnit("miliseconds") 
						    .tags("region", "test") 
						    .tags("hcStatus", "UP") 
						    .tags("status", "NOT PASS") 
						    .tags("message", "Query") 
						    .tags("threshold (ms)", Long.toString(hcThreshold)) 
						    .register(registry);
					summary.record(timeElapsed);

			}
				return;
			}
			catch (Exception ex) {
				long finish = System.currentTimeMillis();
				date = new Date();
				Timestamp endTimestamp = new Timestamp(date.getTime());
		
				long timeElapsed = finish - start;
				builder.up().withDetail("status", "NOT PASS");
				builder.up().withDetail("product", "Cassandra");
//				builder.up().withDetail("version", cassandraReg.getRelease_version());
				builder.up().withDetail("startTimestamp", startTimestamp);
				builder.up().withDetail("endTimestamp", endTimestamp);
				builder.up().withDetail("elapsedTime (ms)", timeElapsed); 
				builder.up().withDetail("threshold (ms)", hcThreshold);
				builder.up().withDetail("message", ex.getLocalizedMessage()); 

				summary = DistributionSummary
					    .builder("hca.cassandra.query."+hcProject)
					    .description("Health Check - Cassandra - L3 - Summary") 
					    .baseUnit("miliseconds") 
					    .tags("region", "test") 
					    .tags("hcStatus", "UP") 
					    .tags("status", "NOT PASS") 
					    .tags("message", "Exception"+ex.getLocalizedMessage()) 
					    .tags("threshold (ms)", Long.toString(hcThreshold)) 
					    .register(registry);
				summary.record(timeElapsed);
			}

		}
}