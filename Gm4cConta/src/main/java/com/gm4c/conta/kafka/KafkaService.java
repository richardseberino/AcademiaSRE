package com.gm4c.conta.kafka;

import java.util.Map.Entry;

import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.gm4c.conta.ContaCorrente;
import com.gm4c.conta.domain.MessagesEnum;
import com.gm4c.logging.factories.ContextLoggerFactory;
import com.gm4c.logging.ports.IContextLogger;
import com.gm4c.utils.KafkaHeaderMap;

import io.opentracing.References;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;

@Service
public class KafkaService {
	
	private static final IContextLogger LOG = ContextLoggerFactory.getLogger(KafkaService.class);

	 public Span startConsumerSpan(String name, MessageHeaders headers, Tracer tracer) {

	    	TextMap carrier = new KafkaHeaderMap(headers);
	        SpanContext parent = tracer.extract(Format.Builtin.TEXT_MAP, carrier);
	        return tracer.buildSpan(name) //
	                .addReference(References.FOLLOWS_FROM, parent) //
	                .start();
	    }

//		public void sendMessage(ContaCorrente message, Span spanPai, String topico, Tracer tracer, org.springframework.kafka.core.KafkaTemplate<String, ContaCorrente> kafkaTemplate, String spanId)
		public void sendMessage(ContaCorrente message, Span spanPai, String topico, Tracer tracer, org.springframework.kafka.core.KafkaTemplate<String, ContaCorrente> kafkaTemplate, String spanId, String idCorrelacao, String idTransacao, Boolean syntheticTransaction)
		{
			LOG.debug(MessagesEnum.GM4C_CTA0004D.getCodAndDescription(), topico);
			KafkaHeaderMap h1 = new KafkaHeaderMap();
			Span span = tracer.buildSpan(spanId).asChildOf(spanPai).start();
			tracer.inject(span.context(), Format.Builtin.TEXT_MAP, h1);
			span.setTag("kafka.mensagem", message.toString());
			span.setTag("kafka.topico", topico); 
			span.setTag("span.kind", "KafkaProducer");
			Entry<String, String> item = h1.getContext();
			Message<ContaCorrente> mensagem = MessageBuilder
					.withPayload(message)
					.setHeader(KafkaHeaders.TOPIC, topico)
					.setHeader("tracer_context_" + item.getKey(), item.getValue())
					.setHeader("correlationId", idCorrelacao)
					.setHeader("transactionId", idTransacao)
					.setHeader("syntheticTransaction", syntheticTransaction)
					.build();
			kafkaTemplate.send(mensagem);
	        LOG.debug(MessagesEnum.GM4C_CTA0003D.getCodAndDescription(), mensagem);
			span.finish();
		}

		

}
