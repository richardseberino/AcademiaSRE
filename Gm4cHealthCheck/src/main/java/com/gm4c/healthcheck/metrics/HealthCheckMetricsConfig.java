//package com.gm4c.healthcheck.metrics;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import com.gm4c.logging.factories.ContextLoggerFactory;
//import com.gm4c.logging.ports.IContextLogger;
//import com.gm4c.logging.domain.MessagesEnum;
//import io.micrometer.core.instrument.DistributionSummary;
//import io.micrometer.core.instrument.MeterRegistry;
//
//@Configuration
//public class HealthCheckMetricsConfig {
//	
//	private static final IContextLogger LOG = ContextLoggerFactory.getLogger(HealthCheckMetricsConfig.class);
//
//	@Autowired
//	MeterRegistry registry;
//	
////	@Autowired
////	DistributionSummary summary;
//	
//	@Bean
//	public void contadorHealthCheckKafkaL3() {
//		DistributionSummary summary = DistributionSummary
////		summary = DistributionSummary
//			    .builder("hca.kafka.socket.ZZZSummary")
//			    .description("Health Check - Kafka - L3 - Summary") // optional
////			    .baseUnit("bytes") // optional (1)
//			    .baseUnit("miliseconds") // optional (1)
//			    .tags("region", "test") // optional
////			    .scale(100) // optional (2)
//			    .register(registry);
//		LOG.info(MessagesEnum.GM4C_TEF0004D.getCodAndDescription(), "hca.kafka.socket.ZZZSummary");
//		registry.counter("hca.kafka.socket.ZZZCounter");
//		LOG.info(MessagesEnum.GM4C_TEF0004D.getCodAndDescription(), "hca.kafka.socket.ZZZCounter");
////	@Bean
////	public DistributionSummary summary = DistributionSummary
////			    .builder("hca.kafka.socket")
////			    .description("Health Check - Kafka - L3 - Summary") // optional
//////			    .baseUnit("bytes") // optional (1)
////			    .baseUnit("miliseconds") // optional (1)
////			    .tags("region", "test") // optional
//////			    .scale(100) // optional (2)
////			    .register(registry);
//////		LOG.debug(MessagesEnum.GM4C_TEF0004D.getCodAndDescription(), "hca");
//
//	}
//}
