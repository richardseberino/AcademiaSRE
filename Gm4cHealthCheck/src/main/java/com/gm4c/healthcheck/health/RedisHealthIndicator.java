package com.gm4c.healthcheck.health;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.redis.connection.ClusterInfo;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;

@Component
//@ConditionalOnProperty(name = "management.health.redis.enabled", havingValue = "true", matchIfMissing = false)
public class RedisHealthIndicator extends AbstractHealthIndicator {

	@Value("${management.health.redis.enabled:false}")
	private Boolean hcEnabled;
	
	@Value("${management.gm4c.name:\"gm4c\"}")
	private String hcProject;

	@Value("${management.health.redis.threshold:1000}")
	private long hcThreshold;

	static final String VERSION = "version";

	static final String REDIS_VERSION = "redis_version";

	private final RedisConnectionFactory redisConnectionFactory;

	@Autowired
	MeterRegistry registry;

	public RedisHealthIndicator(RedisConnectionFactory connectionFactory) {
		super("Redis health check failed");
		Assert.notNull(connectionFactory, "ConnectionFactory must not be null");
		this.redisConnectionFactory = connectionFactory;
	}

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {

		if (!hcEnabled) {
			builder.unknown().withDetail("status", "NOT CHECKED");
			return;
		}
		
		long start = System.currentTimeMillis();
		Date  date = new Date();
		Timestamp startTimestamp = new Timestamp(date.getTime());

		DistributionSummary summary = null;

		RedisConnection connection = RedisConnectionUtils.getConnection(this.redisConnectionFactory);
		long finish = System.currentTimeMillis();
		date = new Date();
		Timestamp endTimestamp = new Timestamp(date.getTime());

		long timeElapsed = finish - start;
		
		try {
			Properties info = connection.info();
			if (connection instanceof RedisClusterConnection) {
				ClusterInfo clusterInfo = ((RedisClusterConnection) connection).clusterGetClusterInfo();
				builder.up().withDetail("status", "PASS");
				builder.up().withDetail("product", "Redis");
				builder.up().withDetail("version", info.getProperty(REDIS_VERSION));
				builder.up().withDetail("Timestamp Start", startTimestamp.toString());
				builder.up().withDetail("Timestamp End", endTimestamp.toString());
				builder.up().withDetail("elapsedTime (ms)", timeElapsed);
				builder.up().withDetail("threshold (ms)", hcThreshold);
//				builder.up().withDetail("message", "");
				builder.up().withDetail("cluster_size", clusterInfo.getClusterSize());
				builder.up().withDetail("slots_up", clusterInfo.getSlotsOk());
				builder.up().withDetail("slots_fail", clusterInfo.getSlotsFail());

				summary = DistributionSummary
					    .builder("hca.redis.connection.cluster."+hcProject)
					    .description("Health Check - Redis - L2 - Summary") 
					    .baseUnit("miliseconds") 
					    .tags("region", "test") 
					    .tags("hcStatus", "UP") 
					    .tags("status", "PASS") 
					    .tags("threshold (ms)", Long.toString(hcThreshold)) 
					    .tags("message", "OK") 
					    .register(registry);
				summary.record(timeElapsed);

			}
			else {
				builder.up().withDetail("status", "PASS");
				builder.up().withDetail("product", "Redis");
				builder.up().withDetail("version", info.getProperty(REDIS_VERSION));
				builder.up().withDetail("Timestamp Start", startTimestamp.toString());
				builder.up().withDetail("Timestamp End", endTimestamp.toString());
				builder.up().withDetail("elapsedTime (ms)", timeElapsed);
				builder.up().withDetail("threshold (ms)", hcThreshold);
//				builder.up().withDetail("message", "");
//				builder.up().withDetail("cluster_size", clusterInfo.getClusterSize());
//				builder.up().withDetail("slots_up", clusterInfo.getSlotsOk());
//				builder.up().withDetail("slots_fail", clusterInfo.getSlotsFail());

				summary = DistributionSummary
					    .builder("hca.redis.connection.notcluster."+hcProject)
					    .description("Health Check - Redis - L2 - Summary") 
					    .baseUnit("miliseconds") 
					    .tags("region", "test") 
					    .tags("hcStatus", "UP") 
					    .tags("status", "PASS") 
					    .tags("threshold (ms)", Long.toString(hcThreshold)) 
					    .tags("message", "OK") 
					    .register(registry);
				summary.record(timeElapsed);

			}
		}
		finally {
			RedisConnectionUtils.releaseConnection(connection, this.redisConnectionFactory, false);
		}
	}

}
