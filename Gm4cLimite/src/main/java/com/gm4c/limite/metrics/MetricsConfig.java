package com.gm4c.limite.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gm4c.limite.domain.MessagesEnum;
import com.gm4c.logging.factories.ContextLoggerFactory;
import com.gm4c.logging.ports.IContextLogger;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class MetricsConfig {
	
	@Autowired
	MeterRegistry registry;
	
	private static final IContextLogger LOG = ContextLoggerFactory.getLogger(MetricsConfig.class);
	
	@Bean
	public Counter contadorAppMessageSubscribeSimulacao() {
		Counter contador = registry.counter("app.message.subscribe", "app", "limite", "fluxo", "simulacao", "topico", "tef");
		LOG.debug(MessagesEnum.GM4C_LIM0001D.getCodAndDescription(), "app.message.subscribe - simulacao");
		return contador;
	}
	
	@Bean
	public Counter contadorAppMessageSubscribeEfetivacao() {
		Counter contador = registry.counter("app.message.subscribe", "app", "limite", "fluxo", "efetivacao", "topico", "tef");
		LOG.debug(MessagesEnum.GM4C_LIM0001D.getCodAndDescription(), "app.message.subscribe - evetivacao");
		return contador;
	}

	
	@Bean
	public void contadorAppMessagePublish() {
		registry.counter("app.message.publish", "app", "limite", "fluxo", "simulacao", "topico", "limite",  "resultado", "ERRO");
		registry.counter("app.message.publish", "app", "limite", "fluxo", "efetivacao", "topico", "limite", "resultado", "ERRO");
		registry.counter("app.message.publish", "app", "limite", "fluxo", "simulacao", "topico", "limite",  "resultado", "SUCESSO");
		registry.counter("app.message.publish", "app", "limite", "fluxo", "efetivacao", "topico", "limite", "resultado", "SUCESSO");
		LOG.debug(MessagesEnum.GM4C_LIM0001D.getCodAndDescription(), "app.message.publish - simulacao");
		LOG.debug(MessagesEnum.GM4C_LIM0001D.getCodAndDescription(), "app.message.publish - evetivacao");
	}

}
