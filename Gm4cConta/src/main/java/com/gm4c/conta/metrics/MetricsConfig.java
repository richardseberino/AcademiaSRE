package com.gm4c.conta.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gm4c.conta.domain.MessagesEnum;
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
		Counter contador = registry.counter("app.message.subscribe", "app", "conta", "fluxo", "simulacao", "topico", "tef");
		LOG.debug(MessagesEnum.GM4C_CTA0001D.getCodAndDescription(), "app.message.subscribe - simulacao");
		return contador;
	}
	
	@Bean
	public Counter contadorAppMessageSubscribeEfetivacao() {
		Counter contador = registry.counter("app.message.subscribe", "app", "conta", "fluxo", "efetivacao", "topico", "tef");
		LOG.debug(MessagesEnum.GM4C_CTA0001D.getCodAndDescription(), "app.message.subscribe - evetivacao");
		return contador;
	}

	
	@Bean
	public void contadorAppMessagePublish() {
		registry.counter("app.message.publish", "app", "conta", "fluxo", "simulacao", "topico", "conta", "resultado", "SUCESSO");
		registry.counter("app.message.publish", "app", "conta", "fluxo", "simulacao", "topico", "conta", "resultado", "ERRO");
		registry.counter("app.message.publish", "app", "conta", "fluxo", "efetivacao", "topico", "conta", "resultado", "SUCESSO"));
		registry.counter("app.message.publish", "app", "conta", "fluxo", "efetivacao", "topico", "conta", "resultado", "ERRO");
		LOG.debug(MessagesEnum.GM4C_CTA0001D.getCodAndDescription(), "app.message.publish - simulacao");
		LOG.debug(MessagesEnum.GM4C_CTA0001D.getCodAndDescription(), "app.message.publish - evetivacao");
	}

}
