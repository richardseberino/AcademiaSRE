package com.gm4c.healthcheck.health;

import static org.springframework.boot.actuate.health.Status.UP;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Component
@RequiredArgsConstructor
//@ConditionalOnProperty(name = "management.health.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class KafkaHealthIndicatorSocket extends AbstractHealthIndicator {

	@Value("${management.health.kafka.enabled:false}")
	private Boolean hcEnabled;

	@Value("${management.gm4c.name:\"gm4c\"}")
	private String hcProject;

	@Value("${management.health.kafka.topic:\"log-brokers\"}")
	private String hcTopic;
	
	@Value("${management.health.kafka.bootstrap-servers:\"faltaservidorkafka\"}")
	private String hcServers;
	
	@Value("${management.health.kafka.read-timeout:1000}")
	private Integer hcTimeout;
		
	@Value("${management.health.kafka.threshold:1000}")
	private Integer hcThreshold;
	  
	private boolean simulaConexao = false;

	@Autowired
	MeterRegistry registry;

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {

		if (!hcEnabled) {
			builder.unknown().withDetail("status", "NOT CHECKED");
			return;
		}
//		if (!hcEnabled) {
//            return Health.unknown()
//                    .withDetail("status", "NOT CHECKED")
//                    .build();
//		}

		DistributionSummary summary = null;
		
		long start = System.currentTimeMillis();
		Date  date = new Date();
		Timestamp startTimestamp = new Timestamp(date.getTime());

		Map<String, String> details = null;
				
		try {
			details = Optional.ofNullable(hcServers)
					.map(this::health)
					.orElseThrow(() -> new Exception ("Exception TBD"));

			long finish = System.currentTimeMillis();
			date = new Date();
			Timestamp endTimestamp = new Timestamp(date.getTime());

			long timeElapsed = finish - start;
			
			if (statusSuccessfull(details)) {
				summary = DistributionSummary
					    .builder("hca.kafka.socket."+hcProject)
					    .description("Health Check - Kafka - L2 - Summary") 
					    .baseUnit("miliseconds") 
					    .tags("region", "test") 
					    .tags("hcStatus", "UP") 
					    .tags("status", "PASS") 
					    .tags("message", "OK") 
					    .tags("threshold (ms)", Long.toString(hcThreshold)) 
					    .register(registry);
				summary.record(timeElapsed);
				builder.up().withDetails(details);

		
			} else {
				summary = DistributionSummary
					    .builder("hca.kafka.socket."+hcProject)
					    .description("Health Check - Kafka - L2 - Summary") 
					    .baseUnit("miliseconds") 
					    .tags("region", "test") 
					    .tags("hcStatus", "DOWN") 
					    .tags("status", "NOT PASS") 
					    .tags("message", details.toString()) 
					    .tags("threshold (ms)", Long.toString(hcThreshold)) 
					    .register(registry);
				summary.record(timeElapsed);
				builder.up().withDetails(details);
//				builder.down().withDetails(details);
			}
			
		} catch (Exception e) {
			long finish = System.currentTimeMillis();
			date = new Date();
			Timestamp endTimestamp = new Timestamp(date.getTime());

			long timeElapsed = finish - start;
			summary = DistributionSummary
				    .builder("hca.kafka.socket."+hcProject)
				    .description("Health Check - Kafka - L2 - Summary") 
				    .baseUnit("miliseconds") 
				    .tags("region", "test") 
				    .tags("hcStatus", "DOWN") 
				    .tags("status", "NOT PASS") 
				    .tags("message", e.toString()) 
				    .tags("threshold (ms)", Long.toString(hcThreshold)) 
				    .register(registry);
			summary.record(timeElapsed);
			builder.up().withDetails(details);
//			builder.down().withDetails(details);
			
		}
		
	}
	
	private Map<String, String> health(String servers) {
		return parseByNode(servers)
				.stream()
				.map(this::parseByHostAndPort)
				.map(this::keepAlive)
				.map(this::parseResult)
				.collect(Collectors.toMap(e -> e[0],  e -> e[1]));
	}
	
	private String keepAlive(String[] hosts) {
		StringBuilder sb = new StringBuilder();
		sb.append(hosts[0]);
		sb.append(":");
		sb.append(hosts[1]);
		sb.append(",");
		sb.append(simulaConexao ? UP.getCode() : verify(hosts[0], Integer.parseInt(hosts[1])));
		return sb.toString();
	}
	
	
	private String verify(String host, Integer port) {
		DistributionSummary summary = null;
		Builder builder = null;
		
		long start = System.currentTimeMillis();
		Date  date = new Date();
		Timestamp startTimestamp = new Timestamp(date.getTime());

		try(Socket socket = new Socket()){
			socket.connect(new InetSocketAddress(host, port), hcTimeout);
			long finish = System.currentTimeMillis();
			date = new Date();
			Timestamp endTimestamp = new Timestamp(date.getTime());

			long timeElapsed = finish - start;

			summary = DistributionSummary
				    .builder("hca.kafka.socket."+hcProject)
				    .description("Health Check - Kafka - L2 - Summary") 
				    .baseUnit("miliseconds") 
				    .tags("region", "test") 
				    .tags("hcStatus", "UP") 
				    .tags("status", "PASS") 
				    .tags("message", "OK") 
				    .tags("threshold (ms)", Long.toString(hcThreshold)) 
				    .register(registry);
			summary.record(timeElapsed);

			builder.up().withDetail("status", "PASS");
			builder.up().withDetail("product", "Kafka");
			builder.up().withDetail("Timestamp Start", startTimestamp.toString());
			builder.up().withDetail("Timestamp End", endTimestamp.toString());
			builder.up().withDetail("elapsedTime (ms)", timeElapsed);
			builder.up().withDetail("threshold (ms)", hcThreshold);
			builder.up().withDetail("message", "OK");

			return UP.getCode();

		} catch (IOException e) {
			long finish = System.currentTimeMillis();
			date = new Date();
			Timestamp endTimestamp = new Timestamp(date.getTime());

			long timeElapsed = finish - start;

			summary = DistributionSummary
				    .builder("hca.kafka.socket."+hcProject)
				    .description("Health Check - Kafka - L2 - Summary") 
				    .baseUnit("miliseconds") 
				    .tags("region", "test") 
				    .tags("hcStatus", "DOWN") 
				    .tags("status", "NOT PASS") 
				    .tags("message", e.toString()) 
				    .tags("threshold (ms)", Long.toString(hcThreshold)) 
				    .register(registry);
			summary.record(timeElapsed);
			//			System.out.println("Exception" + e);

			builder.up().withDetail("status", "NOT PASS");
			builder.up().withDetail("product", "Kafka");
			builder.up().withDetail("Timestamp Start", startTimestamp.toString());
			builder.up().withDetail("Timestamp End", endTimestamp.toString());
			builder.up().withDetail("elapsedTime (ms)", timeElapsed);
			builder.up().withDetail("threshold (ms)", hcThreshold);
			builder.up().withDetail("message", e.toString());
			return UP.getCode();

			//			return DOWN.getCode();
		}
	}
	
	private boolean statusSuccessfull(Map<String, String> result) {
		return result.entrySet()
				.stream()
				.anyMatch(this::statusUp);
	}

	private boolean statusUp(Map.Entry<String, String> entry) {
		return entry.getValue().equals(UP.getCode());
	}

	private List<String> parseByNode(String servers) {
		return Arrays.asList(servers.split(","));
	}
	
	private String[] parseByHostAndPort(String hosts) {
		return hosts.split(":");
	}
	
	private String[] parseResult(String hosts) {
		return hosts.split(",");
	}
		
}
