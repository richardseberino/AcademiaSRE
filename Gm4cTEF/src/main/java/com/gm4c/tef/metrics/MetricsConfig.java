package com.gm4c.tef.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gm4c.logging.factories.ContextLoggerFactory;
import com.gm4c.logging.ports.IContextLogger;
import com.gm4c.tef.domain.MessagesEnum;

import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class MetricsConfig {
	
	private static final IContextLogger LOG = ContextLoggerFactory.getLogger(MetricsConfig.class);
	@Autowired
	MeterRegistry registry;
	
	
	@Bean
	public void contadorAppMessageSubscribe() {
		registry.counter("app.message.subscribe", "app", "tef", "fluxo", "simulacao", "topico", "senha");
		registry.counter("app.message.subscribe", "app", "tef", "fluxo", "simulacao", "topico", "limite");
		registry.counter("app.message.subscribe", "app", "tef", "fluxo", "simulacao", "topico", "conta");
		registry.counter("app.message.subscribe", "app", "tef", "fluxo", "efetivacao", "topico", "limite");
		registry.counter("app.message.subscribe", "app", "tef", "fluxo", "efetivacao", "topico", "conta");
		LOG.debug(MessagesEnum.GM4C_TEF0004D.getCodAndDescription(), "app.message.subscribe");
	}

	@Bean
	public void contadorAppMessagePublish() {
		registry.counter("app.message.publish", "app", "tef", "fluxo", "simulacao", "topico", "tef");
		registry.counter("app.message.publish", "app", "tef", "fluxo", "efetivacao", "topico", "tef");
		LOG.debug(MessagesEnum.GM4C_TEF0004D.getCodAndDescription(), "app.message.publish");
	}

}
