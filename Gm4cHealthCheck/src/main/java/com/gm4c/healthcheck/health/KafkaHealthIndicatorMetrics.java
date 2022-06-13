package com.gm4c.healthcheck.health;


import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Gauge.Builder;
import io.micrometer.core.instrument.MeterRegistry;

@Component
//@ConditionalOnProperty(name = "management.health.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class KafkaHealthIndicatorMetrics implements HealthIndicator {

	@Value("${management.health.kafka.enabled:false}")
	private Boolean hcEnabled;

	@Autowired
	private KafkaAdmin admin;
	
	@Autowired
	private MeterRegistry meterRegistry;
	
	@Autowired
	private Map<String, KafkaTemplate<?, ?>> kafkaTemplates;
	
//	@Bean
	public AdminClient kafkaAdminClientNew() {
	    return AdminClient.create(admin.getConfig());
	}
	
	@SuppressWarnings("deprecation") // Can be avoided by relying on Double.NaN for non doubles.
	@PostConstruct
	private void initMetrics() {
		final String kafkaPrefix = "kafkaProducer.";
	    for (Entry<String, KafkaTemplate<?, ?>> templateEntry : kafkaTemplates.entrySet()) {
	        final String name = templateEntry.getKey();
	        final KafkaTemplate<?, ?> kafkaTemplate = templateEntry.getValue();
	        for (Metric metric : kafkaTemplate.metrics().values()) {
	            final MetricName metricName = metric.metricName();
	            final Builder<Metric> gaugeBuilder = Gauge
	                    .builder(kafkaPrefix + metricName.name(), metric, Metric::value) // <-- Here
	                    .description(metricName.description());
	            for (Entry<String, String> tagEntry : metricName.tags().entrySet()) {
	                gaugeBuilder.tag(kafkaPrefix + tagEntry.getKey(), tagEntry.getValue());
	            }
	    		gaugeBuilder.tag("beanKafkaProducer", name);
	            gaugeBuilder.register(meterRegistry);
	        }
	    }
	}
	
//	@Bean
	@Override
	public Health health(){
		
		if (!hcEnabled) {
            return Health.unknown()
                    .withDetail("status", "NOT CHECKED")
                    .build();
		}

		final DescribeClusterOptions describeClusterOptions = new DescribeClusterOptions().timeoutMs(1000);
	    final AdminClient adminClient = kafkaAdminClientNew();
//	    return () -> {
	        final DescribeClusterResult describeCluster = adminClient.describeCluster(describeClusterOptions);
	        try {
	            final String clusterId = describeCluster.clusterId().get();
	            final int nodeCount = describeCluster.nodes().get().size();
//	            final String nodeCountS = describeCluster.nodes().toString();
	            return Health.up()
	                    .withDetail("clusterId", clusterId)
	                    .withDetail("nodeCount", nodeCount)
	                    .build();
	        } catch (InterruptedException | ExecutionException e) {
	            return Health.down()
	                    .withException(e)
	                    .build();
	        }
//	    };
	}
}