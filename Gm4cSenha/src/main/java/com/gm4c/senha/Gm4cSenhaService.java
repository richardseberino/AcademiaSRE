package com.gm4c.senha;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import com.gm4c.logging.factories.ContextLoggerFactory;
import com.gm4c.logging.ports.IContextLogger;
import com.gm4c.logging.types.MdcType;
import com.gm4c.logging.types.MessageText;
import com.gm4c.senha.domain.MessagesEnum;
import com.gm4c.senha.dto.SenhaDto;
import com.gm4c.senha.dto.SenhaRepositorio;
import com.gm4c.senha.kafka.KafkaService;
import com.gm4c.tef.Transferencia;
import com.gm4c.utils.SpanDatastore;
import com.google.gson.Gson;

import java.time.Duration;
import java.time.OffsetDateTime;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;

import io.opentracing.Span;
import io.opentracing.Tracer;

//@Service
@Service("Senha")
public class Gm4cSenhaService {

	private static final IContextLogger LOG = ContextLoggerFactory.getLogger(Gm4cSenhaService.class);
	
	@Autowired
	SenhaRepositorio repSenha;

	@Autowired
	private final KafkaTemplate<String, Senha> kafkaSenha;

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

	//	public SenhaService(KafkaTemplate<String, Senha> k1, Tracer tracer2)
	public Gm4cSenhaService(KafkaTemplate<String, Senha> k1)
	{
		this.kafkaSenha = k1;
	}
	
	//	@CollectKafkaTrace(spanName = "[Senha] ValidaSenha")
	@KafkaListener(topics="tef", groupId = "senha")
	public void validaSenha(ConsumerRecord<String, Transferencia> record, @Headers MessageHeaders headers)
	{



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


		Object t1 = record.value();
		Transferencia transferencia = new Gson().fromJson(t1.toString(), Transferencia.class);
		
		LOG.setContext(MdcType.TRANSACTION_ID, transferencia.getIdTransacao()); //Transaction
		LOG.setContext(MdcType.CORRELATION_ID, correlationId); //Correlation
		LOG.debug("Início da Validação do Senha");
		LOG.info(record.value(), MessageText.EVENT_RECEIVED);
		//Span span = spanDatastore.getCurrentSpan();
		Span span = kafka.startConsumerSpan("ValidaSenha", headers, tracer);
		span.setTag("evento",transferencia.getEvento());
		


		//verifica se for efetivacao, não faz nada
		if (transferencia.getEvento().equalsIgnoreCase("efetivacao"))
		{
			LOG.info(MessagesEnum.GM4C_SEN0001I.getCodAndDescription());
			span.log("Evento de efetivação, senha nao precisa ser validada, ignorando mensagem");
			span.finish();
			return;
		}
		OffsetDateTime dataHoraInicial = OffsetDateTime.now();
		DistributionSummary summary = DistributionSummary.builder("app.request.duration")
				.baseUnit("milliseconds")
				.tags("app","senha","operacao","validaSenha")
				.register( registry );

		boolean aprovado = false;
		
		SenhaDto senha=null;
		
		//verificando agencia conta e dv  (busca o registro pelos 3 campos>
		try
		{
			LOG.debug("Validando a senha para a Agência {} - Conta {} - DV {}", transferencia.getAgenciaOrigem(), transferencia.getContaOrigem(), transferencia.getDvOrigem());
			span.log("Vai pesquisar as credenciais da conta na base de dados");
			senha = repSenha.pesquisaPorAgenciaConta(transferencia.getAgenciaOrigem(), transferencia.getContaOrigem(), transferencia.getDvOrigem()).get(0);
			span.log("Credenciais encontradas para a conta corrente");
		}
		catch (Exception e)
		{
			span.log("Senha nao cadastrada para essa Conta ou conta nao existe! " + e.getMessage());
			LOG.error(MessagesEnum.GM4C_SEN0001E.getCodAndDescription());
			span.setTag("error", true);
			aprovado = false;
		}
		
		if (senha!=null && senha.getSenha().equals(transferencia.getSenha()))
		{
			span.log("Senha correta");
			LOG.info(MessagesEnum.GM4C_SEN0002I.getCodAndDescription());			
			aprovado = true;
		}
		if (senha!=null && aprovado==false)
		{
			span.log("Senha inválida");
			LOG.info(MessagesEnum.GM4C_SEN0003I.getCodAndDescription());
		}

		try
		{
			Thread.sleep(500);
		}
		catch (Exception e)
		{
			LOG.error("Falha ao simular problema de latencia! " + e.getMessage() );
		}
		span.log("Preparando a mensagem Kafka de retorno do serviço. ");
		//prepara o registro do avro sobre o retorno da senha
		Senha senhaResp = Senha.newBuilder()
				.setAgencia(transferencia.getAgenciaOrigem())
				.setConta(transferencia.getContaOrigem())
				.setDv(transferencia.getDvOrigem())
				.setAprovado(aprovado)
				.setEvento(transferencia.getEvento())
				.setIdSimulacao(transferencia.getIdTransacao())
				.build();
		LOG.info(MessagesEnum.GM4C_SEN0004I.getCodAndDescription());
		//envia a respota da senha para o kafka no topico senha
		kafka.sendMessage(senhaResp, span, "senha", tracer, kafkaSenha, "EnviaRespostaValidacaoSenha", correlationId, transactionId, syntheticTransaction);
		span.log("Mensagem enviada ao kafka, topico: senha");
		span.setTag("mensagem.retorno", senhaResp.toString());
		LOG.info(senhaResp, MessageText.EVENT_PRODUCED);
		span.finish();
		OffsetDateTime dataHoraFinal = OffsetDateTime.now();
		long diferencaTempo = Duration.between(dataHoraInicial, dataHoraFinal).toMillis();
		summary.record( diferencaTempo );
		String resultado = "SUCESSO";
		if (!aprovado)
		{
			resultado = "ERRO";
		}
		Metrics.counter("app.message.publish", "app", "senha", "fluxo", transferencia.getEvento(), "topico","senha", "resultado",resultado ).increment();
		LOG.debug("Fim do método validaSenha");
		LOG.clearContext();
		
	}
}
