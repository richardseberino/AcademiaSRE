package com.gm4c.healthcheck.health;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.TopicPartitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;

@Component
//@ConditionalOnProperty(name = "management.health.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class KafkaHealthIndicatorCluster implements HealthIndicator {

	@Value("${management.health.kafka.enabled:false}")
	private Boolean hcEnabled;

	@Value("${management.gm4c.name:\"gm4c\"}")
	private String hcProject;

	private KafkaAdmin kafkaAdmin;

	private Map<String, Object> kafkaConfig;

	@Value("${management.health.kafka.topic:\"log-brokers\"}")
	private String hcTopic;

	@Value("${management.health.kafka.bootstrap-servers:\"faltaservidorkafka\"}")
	private String hcServers;

	@Value("${management.health.kafka.read-timeout:1000}")
	private Integer hcTimeout;
		
	@Value("${management.health.kafka.threshold:1000}")
	private Integer hcThreshold;

	@Autowired
	MeterRegistry registry;

	public KafkaHealthIndicatorCluster(KafkaAdmin kafkaAdmin) {
		this.kafkaAdmin = kafkaAdmin;

	}	

	@PostConstruct
	public void setUpAdminClient() {
		kafkaConfig = new HashMap<>();
		kafkaConfig.putAll(kafkaAdmin.getConfig());
		kafkaConfig.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, hcServers);
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

		try (AdminClient adminClient = AdminClient.create(kafkaConfig)) {

			DescribeClusterOptions describeClusterOptions = new DescribeClusterOptions().timeoutMs(2000);
			adminClient.describeCluster(describeClusterOptions);

//	  		adminClient.describeConsumerGroups(List.of("topic")).all()
			adminClient.describeConsumerGroups(Arrays.asList("topic")).all()
				.get(2, TimeUnit.SECONDS);

//	      	String groupId = "group-0"; 
//			DescribeConsumerGroupsResult describeResult = adminClient.describeConsumerGroups(Arrays.asList(groupId),
//  			(new DescribeConsumerGroupsOptions()).timeoutMs(10 * 1000));

			Map<String, TopicDescription> topicDescriptionMap = adminClient
//				.describeTopics(List.of(hcTopicName)).all().get(2, TimeUnit.SECONDS);
				.describeTopics(Arrays.asList(hcTopic)).all().get(2, TimeUnit.SECONDS);

			List<TopicPartitionInfo> partitions = topicDescriptionMap.get(hcTopic)
				.partitions();

			long finish = System.currentTimeMillis();
			date = new Date();
			Timestamp endTimestamp = new Timestamp(date.getTime());
			long timeElapsed = finish - start;

			if (partitions == null || partitions.isEmpty()) {
	    		summary = DistributionSummary
	    				.builder("hca.kafka.cluster."+hcProject)
					    .description("Health Check - Kafka - L2 - Summary") 
					    .baseUnit("miliseconds") 
					    .tags("region", "test") 
					    .tags("hcStatus", "UP") 
					    .tags("status", "NOT PASS") 
					    .tags("threshold (ms)", Long.toString(hcThreshold)) 
					    .tags("message", "Kafka healthcheck failed - No partition found for topic: "+hcTopic)
					    .register(registry);
				summary.record(timeElapsed);
//		    	return Health.down()
				return Health.up()
	    			  .withDetail("Kafka healthcheck failed", "No partition found for topic: " + hcTopic)
	    			  .build();
			} else {
	    	  	if (partitions.stream().anyMatch(p -> p.leader() == null)) {
		    		summary = DistributionSummary
		    				.builder("hca.kafka.cluster."+hcProject)
						    .description("Health Check - Kafka - L2 - Summary") 
						    .baseUnit("miliseconds") 
						    .tags("region", "test") 
						    .tags("hcStatus", "UP") 
						    .tags("status", "NOT PASS") 
						    .tags("threshold (ms)", Long.toString(hcThreshold)) 
						    .tags("message", "Kafka healthcheck failed - No partition leader found for topic: " + hcTopic)
						    .register(registry);
					summary.record(timeElapsed);
// 		         	return Health.down().withDetail("Kafka healthcheck failed",
					return Health.up().withDetail("Kafka healthcheck failed",
						"No partition leader found for topic: " + hcTopic).build();
	    	  	}
			}
	    } catch (Exception e) {
//	      	return Health.down()
			long finish = System.currentTimeMillis();
			date = new Date();
			Timestamp endTimestamp = new Timestamp(date.getTime());
			long timeElapsed = finish - start;

			summary = DistributionSummary
				.builder("hca.kafka.cluster."+hcProject)
			    .description("Health Check - Kafka - L2 - Summary") 
			    .baseUnit("miliseconds") 
			    .tags("region", "test") 
			    .tags("hcStatus", "UP") 
			    .tags("status", "NOT PASS") 
			    .tags("threshold (ms)", Long.toString(hcThreshold)) 
			    .tags("message", "Kafka healthcheck failed"+e)
			    .register(registry);
	    	summary.record(timeElapsed);
	    	return Health.up()
	    		.withDetail("Kafka healthcheck failed", "Exception occurred during healthcheck").build();
	    }
//	    System.out.println(System.currentTimeMillis() - start);
		long finish = System.currentTimeMillis();
		date = new Date();
		Timestamp endTimestamp = new Timestamp(date.getTime());
		long timeElapsed = finish - start;
		summary = DistributionSummary
				.builder("hca.kafka.cluster."+hcProject)
			    .description("Health Check - Kafka - L2 - Summary") 
			    .baseUnit("miliseconds") 
			    .tags("region", "test") 
			    .tags("hcStatus", "UP") 
			    .tags("status", "PASS") 
			    .tags("threshold (ms)", Long.toString(hcThreshold)) 
			    .tags("message", "OK")
			    .register(registry);
	    	summary.record(timeElapsed);
	    return Health.up().build();
	  }
	}