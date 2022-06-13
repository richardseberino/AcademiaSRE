package com.gm4c.senha.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gm4c.logging.factories.ContextLoggerFactory;
import com.gm4c.logging.ports.IContextLogger;
import com.gm4c.senha.domain.MessagesEnum;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class MetricsConfig {
	
	@Autowired
	MeterRegistry registry;
	
	private static final IContextLogger LOG = ContextLoggerFactory.getLogger(MetricsConfig.class);

	@Bean
	public Counter contadorAppMessageSubscribeSimulacao() {
		Counter contador = registry.counter("app.message.subscribe", "app", "senha", "fluxo", "simulacao", "topico", "tef");
		LOG.debug(MessagesEnum.GM4C_SEN0001D.getCodAndDescription(), "app.message.subscribe - simulacao");
		return contador;
	}
	
	@Bean
	public Counter contadorAppMessageSubscribeEfetivacao() {
		Counter contador = registry.counter("app.message.subscribe", "app", "senha", "fluxo", "efetivacao", "topico", "tef");
		LOG.debug(MessagesEnum.GM4C_SEN0001D.getCodAndDescription(), "app.message.subscribe - evetivacao");
		return contador;
	}

	
	@Bean
	public void contadorAppMessagePublish() {
		registry.counter("app.message.publish", "app", "senha", "fluxo", "simulacao", "topico", "senha");
		registry.counter("app.message.publish", "app", "senha", "fluxo", "efetivacao", "topico", "senha");
		LOG.debug(MessagesEnum.GM4C_SEN0001D.getCodAndDescription(), "app.message.publish - simulacao");
		LOG.debug(MessagesEnum.GM4C_SEN0001D.getCodAndDescription(), "app.message.publish - evetivacao");
	}

}
