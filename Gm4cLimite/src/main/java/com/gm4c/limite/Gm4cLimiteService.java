package com.gm4c.limite;

import java.time.Duration;
import java.time.OffsetDateTime;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import com.gm4c.limite.domain.MessagesEnum;
import com.gm4c.limite.dto.LimiteDto;
import com.gm4c.limite.dto.LimiteRepositorio;
import com.gm4c.limite.kafka.KafkaService;
import com.gm4c.logging.factories.ContextLoggerFactory;
import com.gm4c.logging.ports.IContextLogger;
import com.gm4c.logging.types.MdcType;
import com.gm4c.logging.types.MessageText;
import com.gm4c.tef.Transferencia;
import com.google.gson.Gson;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.opentracing.Span;
import io.opentracing.Tracer;

//@Service
@Service("Limite")
public class Gm4cLimiteService {

	private static final IContextLogger LOG = ContextLoggerFactory.getLogger(Gm4cLimiteService.class);
	
	@Autowired
	LimiteRepositorio replimite;

	@Autowired
	private final KafkaTemplate<String, Limite> kafkaLimite;

	private KafkaService kafka = new KafkaService();
	
	@Autowired
	private Tracer tracer;
	
	
	@Autowired
	MeterRegistry registry;
	
	@Autowired
	Counter contadorAppMessageSubscribeSimulacao;
	
	@Autowired
	Counter contadorAppMessageSubscribeEfetivacao;

	private String correlationId;
	private String transactionId;
	private boolean syntheticTransaction;
	
	
	public Gm4cLimiteService(KafkaTemplate<String, Limite> k1)
	{
		this.kafkaLimite = k1;
	}
	
	@KafkaListener(topics="tef", groupId = "limite")
	public void validaLimite(ConsumerRecord<String, Transferencia> record, @Headers MessageHeaders headers)
	{

		OffsetDateTime dataHoraInicial = OffsetDateTime.now();
		DistributionSummary summary = DistributionSummary.builder("app.request.duration")
				.baseUnit("milliseconds")
				.tags("app","limite","operacao","validaLimite")
				.register( registry );
		
		Object t1 = record.value();
		Transferencia transferencia = new Gson().fromJson(t1.toString(), Transferencia.class);
		
		headers.keySet().forEach(key -> {
            Object value = headers.get(key);
            if (key.equalsIgnoreCase("correlationId")){
            	correlationId = (String) value;
            }else if (key.equalsIgnoreCase("transactionId")){
            	transactionId = (String) value;
            }else if (key.equalsIgnoreCase("syntheticTransaction")){
            	syntheticTransaction = (Boolean) value;
            } 

        });

		if (syntheticTransaction == true) {
			//LOG.warn(MessageText.SYNTHETIC_TRANSACTION);
			return;
		}

		
		LOG.setContext(MdcType.TRANSACTION_ID, transferencia.getIdTransacao()); //Transaction
		LOG.setContext(MdcType.CORRELATION_ID, correlationId); //Correlation
		LOG.debug("Início da Validação do Limite");
		LOG.info(record.value(), MessageText.EVENT_RECEIVED);
		//Span span = spanDatastore.getCurrentSpan();
		Span span = kafka.startConsumerSpan("ValidaLimite", headers, tracer);
		span.setTag("evento",transferencia.getEvento());
		
		span.setTag("transaction_id", transactionId);
		span.setTag("correlationId", correlationId);
		boolean aprovado = true;

		LimiteDto dadosLimite = null;
		
		try 
		{

			LOG.debug(MessagesEnum.GM4C_LIM0001I.getCodAndDescription(),transferencia.getAgenciaOrigem(),transferencia.getContaOrigem(),transferencia.getDvOrigem());
			span.log("Recuperando os dados de limite da conta origem!");
			dadosLimite = replimite.pesquisaLimite(transferencia.getAgenciaOrigem(), transferencia.getContaOrigem(), transferencia.getDvOrigem()).get(0); 
			span.log("Dado do limite recuperados da base cassandra!");
			LOG.info(dadosLimite, MessagesEnum.GM4C_LIM0002I.getCodAndDescription());
			
			if (transferencia.getEvento().equalsIgnoreCase("efetivacao"))
			{
				dadosLimite.setValor_utilizado(dadosLimite.getValor_utilizado()+transferencia.getValor());
				replimite.save(dadosLimite);
				LOG.info(dadosLimite.getValor_utilizado(),MessagesEnum.GM4C_LIM0003I.getCodAndDescription());
				span.log("limite atualiado! valor utilizado atualizado: " + dadosLimite.getValor_utilizado());
				contadorAppMessageSubscribeEfetivacao.increment();
				LOG.debug(MessagesEnum.GM4C_LIM0004D.getCodAndDescription(), "contadorAppMessageSubscribeEfetivacao");
			}
			else //simulacao
			{
				if  ((dadosLimite.getValor_limite()- dadosLimite.getValor_utilizado())>=transferencia.getValor())
				{
					aprovado = true;
					span.log("Limite aprovado! valor da transacao: " + transferencia.getValor() +", limite disponível: " + (dadosLimite.getValor_limite()-dadosLimite.getValor_utilizado()));
					LOG.info((dadosLimite.getValor_limite()-dadosLimite.getValor_utilizado()),MessagesEnum.GM4C_LIM0004I.getCodAndDescription());
				} else
				{
					span.log("Limite insuficiente! valor da transacao: " + transferencia.getValor() +", limite disponível: " + (dadosLimite.getValor_limite()-dadosLimite.getValor_utilizado()));
					aprovado = false;
					span.setTag("error", true);
					LOG.info((dadosLimite.getValor_limite()-dadosLimite.getValor_utilizado()),MessagesEnum.GM4C_LIM0005I.getCodAndDescription());
				}
				contadorAppMessageSubscribeSimulacao.increment();
				LOG.debug(MessagesEnum.GM4C_LIM0004D.getCodAndDescription(), "contadorAppMessageSubscribeSimulacao");
			}

		}
		catch (Exception e)
		{ 
			span.log("Erro: " + e.getMessage());
			span.setTag("error", true);
			aprovado = false;
			LOG.error(MessagesEnum.GM4C_LIM0001E.getCodAndDescription() + " ["+ e.getMessage() + "]", e);
		}
		LOG.info(MessagesEnum.GM4C_LIM0006I.getCodAndDescription());
		span.log("Preparando a resposta AVRO do limite");
		//prepara o registro do avro sobre o retorno do limite
		Limite limite = Limite.newBuilder()
				.setAgencia(transferencia.getAgenciaOrigem())
				.setConta(transferencia.getContaOrigem())
				.setDv(transferencia.getDvOrigem())
				.setValor(transferencia.getValor())
				.setIdSimulacao(transferencia.getIdTransacao())
				.setEvento(transferencia.getEvento())
				.setAprovado(aprovado)
				.build();
		span.log("Mensagem AVRO preparada, fazendo envio no Kafka");
		LOG.info(MessagesEnum.GM4C_LIM0007I.getCodAndDescription());
		//envia a respota do limite para o kafka no topico limite
		kafka.sendMessage(limite, span, "limite", tracer, kafkaLimite, "EnviaRespostaLimite", correlationId, transactionId, syntheticTransaction);
		span.log("Mensagem enviada ao Kafka");
		LOG.info(limite, MessageText.EVENT_PRODUCED);

		
		span.finish();
		LOG.debug("Fim do método validaLimite");
		LOG.clearContext();
		String resultado = "SUCESSO";
		if (!aprovado)
		{
			resultado = "ERRO";
		}
		Metrics.counter("app.message.publish", "app", "limite", "fluxo", transferencia.getEvento(), "topico","limite","resultado",resultado).increment();
		OffsetDateTime dataHoraFinal = OffsetDateTime.now();
		long diferencaTempo = Duration.between(dataHoraInicial, dataHoraFinal).toMillis();
		summary.record( diferencaTempo );
	}
		
}