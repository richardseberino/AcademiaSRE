package com.gm4c.healthcheck.health;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;

@Component
//@ConditionalOnProperty(name = "management.health.h2.enabled", havingValue = "true", matchIfMissing = false)
@ConditionalOnEnabledHealthIndicator("db")
public class DbHealthIndicator extends AbstractHealthIndicator {
	//public class DataSourceHealthIndicator extends AbstractHealthIndicator implements InitializingBean {
	
	@Value("${management.health.h2.enabled:false}")
	private Boolean hcEnabled;
	
	@Value("${management.gm4c.name:\"gm4c\"}")
	private String hcProject;

	@Value("${management.health.h2.threshold:1000}")
	private Long hcThreshold;

	@Value("${management.health.h2.query:\"SELECT VALUE FROM INFORMATION_SCHEMA.SETTINGS  WHERE NAME=\'info.VERSION\'\"}")
	private String hc_query;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	MeterRegistry registry;

	@Bean
	@ConditionalOnMissingBean(name = { "dbHealthIndicator", "dbHealthContributor" })
	protected void doHealthCheck(Health.Builder builder) throws Exception {

		if (!hcEnabled) {
			builder.unknown().withDetail("status", "NOT CHECKED");
			return;
		}

		String product = getProduct();

		DistributionSummary summary = null;

		long start = System.currentTimeMillis();
		Date  date = new Date();
		Timestamp startTimestamp = new Timestamp(date.getTime());

		try {
			List<Object> listH2Versions = this.jdbcTemplate.query(hc_query, new SingleColumnRowMapper());

			long finish = System.currentTimeMillis();
			date = new Date();
			Timestamp endTimestamp = new Timestamp(date.getTime());
			long timeElapsed = finish - start;

			Object h2Version = DataAccessUtils.requiredSingleResult(listH2Versions);
			if (timeElapsed <= hcThreshold) {
				builder.up().withDetail("status", "PASS");
				builder.up().withDetail("product", product);
				builder.up().withDetail("version", h2Version);
				builder.up().withDetail("Timestamp Start", startTimestamp.toString());
				builder.up().withDetail("Timestamp End", endTimestamp.toString());
				builder.up().withDetail("elapsedTime (ms)", timeElapsed);
				builder.up().withDetail("threshold (ms)", hcThreshold);
//				builder.up().withDetail("message", "");
				builder.up().withDetail("query", hc_query);

				summary = DistributionSummary
					    .builder("hca.h2.query."+hcProject)
					    .description("Health Check - h2 - L3 - Summary") 
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
				builder.up().withDetail("version", h2Version);
				builder.up().withDetail("startTimestamp", startTimestamp);
				builder.up().withDetail("endTimestamp", endTimestamp);
				builder.up().withDetail("elapsedTime (ms)", timeElapsed); 
				builder.up().withDetail("threshold (ms)", hcThreshold);
				builder.up().withDetail("message", "Threshold"); 

				summary = DistributionSummary
					    .builder("hca.h2.query."+hcProject)
					    .description("Health Check - h2 - L3 - Summary") 
					    .baseUnit("miliseconds") 
					    .tags("region", "test") 
					    .tags("hcStatus", "UP") 
					    .tags("status", "NOT PASS") 
					    .tags("status", "Threshold")
					    .tags("threshold (ms)", Long.toString(hcThreshold)) 
					    .tags("message", "Trhe") 
					    .register(registry);
				summary.record(timeElapsed);
				
			}
		}
		catch (Exception ex) {
			long finish = System.currentTimeMillis();
			date = new Date();
			Timestamp endTimestamp = new Timestamp(date.getTime());
			long timeElapsed = finish - start;
			builder.down().withDetail("status", "NOT PASS");
			builder.down().withDetail("product", product);
//			builder.down().withDetail("version", cassandraReg.getRelease_version());
			builder.down().withDetail("startTimestamp", startTimestamp);
			builder.down().withDetail("endTimestamp", endTimestamp);
			builder.down().withDetail("elapsedTime (ms)", timeElapsed); 
			builder.down().withDetail("threshold (ms)", hcThreshold);
			builder.down().withDetail("message", ex.getLocalizedMessage()); 
			builder.down().withDetail("query", hc_query);

			summary = DistributionSummary
				    .builder("hca.h2.query."+hcProject)
				    .description("Health Check - h2 - L3 - Summary") 
				    .baseUnit("miliseconds") 
				    .tags("region", "test") 
				    .tags("hcStatus", "DOWN") 
				    .tags("status", "NOT PASS") 
				    .tags("status", "Threshold")
				    .tags("threshold (ms)", Long.toString(hcThreshold)) 
				    .tags("message", ex.getLocalizedMessage())
				    .register(registry);
			summary.record(timeElapsed);
			
		}
	}

	private String getProduct() {
		return this.jdbcTemplate.execute((ConnectionCallback<String>) this::getProduct);
	}

	private String getProduct(Connection connection) throws SQLException {
		return connection.getMetaData().getDatabaseProductName();
	}

	private static class SingleColumnRowMapper implements RowMapper<Object> {
		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			ResultSetMetaData metaData = rs.getMetaData();
			int columns = metaData.getColumnCount();
			if (columns != 1) {
				throw new IncorrectResultSetColumnCountException(1, columns);
			}
			return JdbcUtils.getResultSetValue(rs, 1);
		}

	}

}
