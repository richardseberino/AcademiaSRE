package com.gm4c.tef.kafka;

import java.util.Map.Entry;

import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.gm4c.logging.factories.ContextLoggerFactory;
import com.gm4c.logging.ports.IContextLogger;
import com.gm4c.tef.Transferencia;
import com.gm4c.tef.domain.MessagesEnum;
import com.gm4c.utils.KafkaHeaderMap;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;

@Service
public class KafkaService {
	
	private static final IContextLogger LOG = ContextLoggerFactory.getLogger(KafkaService.class);

		public void sendMessage(Transferencia message, Span spanPai, String topico, Tracer tracer, org.springframework.kafka.core.KafkaTemplate<String, Transferencia> kafkaTemplate, String spanId, String idCorrelacao, String idTransacao, Boolean syntheticTransaction)
		{
			LOG.debug(MessagesEnum.GM4C_TEF0006D.getCodAndDescription(), topico);
			KafkaHeaderMap h1 = new KafkaHeaderMap();
			Span span = null;
			if (spanPai!=null)
			{
				span = tracer.buildSpan(spanId).asChildOf(spanPai).start();
				tracer.inject(span.context(), Format.Builtin.TEXT_MAP, h1);
				span.setTag("kafka.mensagem", message.toString());
				span.setTag("kafka.topico", topico); 
				span.setTag("span.kind", "KafkaProducer");
			}
			Entry<String, String> item = h1.getContext();
			Message<Transferencia> mensagem = MessageBuilder
					.withPayload(message)
					.setHeader(KafkaHeaders.TOPIC, topico)
					.setHeader("tracer_context_" + item.getKey(), item.getValue())
					.setHeader("correlationId", idCorrelacao)
					.setHeader("transactionId", idTransacao)
					.setHeader("syntheticTransaction", syntheticTransaction)
					.build();
			LOG.debug("0001-A","MenssageBuilder Concluido, iniciando envio para o Kafka");
			kafkaTemplate.send(mensagem);
	        LOG.debug(MessagesEnum.GM4C_TEF0003D.getCodAndDescription(), mensagem);
			if (spanPai!=null)
			{
				span.finish();
			}
		}

}
