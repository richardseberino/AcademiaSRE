package com.gm4c.conta;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import com.gm4c.conta.domain.MessagesEnum;
import com.gm4c.conta.dto.ContaCorrenteDto;
import com.gm4c.conta.dto.ContaRepositorio;
import com.gm4c.conta.kafka.KafkaService;
import com.gm4c.logging.factories.ContextLoggerFactory;
import com.gm4c.logging.ports.IContextLogger;
import com.gm4c.logging.types.MdcType;
import com.gm4c.logging.types.MessageText;
import com.gm4c.tef.Transferencia;
import com.gm4c.utils.SpanDatastore;
import com.google.gson.Gson;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.time.OffsetDateTime;
import io.micrometer.core.instrument.DistributionSummary;
import io.opentracing.Span;
import io.opentracing.Tracer;

//@Service
@Service("Conta")
public class Gm4cContaService {
	
	private static final IContextLogger LOG = ContextLoggerFactory.getLogger(Gm4cContaService.class);
	
	@Autowired
	ContaRepositorio repConta;
	
	@Autowired
	private final KafkaTemplate<String, ContaCorrente> kafkaConta;

	private KafkaService kafka = new KafkaService();
	
	@Autowired
	private Tracer tracer;

	@Autowired
	Counter contadorAppMessageSubscribeSimulacao;
	
	@Autowired
	Counter contadorAppMessageSubscribeEfetivacao;
	
	@Autowired
	MeterRegistry registry;

	private String correlationId;
	private String transactionId;
	private boolean syntheticTransaction;
	

	public Gm4cContaService(KafkaTemplate<String, ContaCorrente> k1)
	{
		this.kafkaConta = k1;
	}
	
	@KafkaListener(topics="tef", groupId = "conta")
	public void validaConta(ConsumerRecord<String, Transferencia> record, @Headers MessageHeaders headers)
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
		OffsetDateTime dataHoraInicial = OffsetDateTime.now();
		DistributionSummary summary = DistributionSummary.builder("app.request.duration")
				.baseUnit("milliseconds")
				.tags("app","conta","operacao","validaConta")
				.register( registry );

		
		Object t1 = record.value();
		Transferencia transferencia = new Gson().fromJson(t1.toString(), Transferencia.class);	
		
		LOG.setContext(MdcType.TRANSACTION_ID, transferencia.getIdTransacao()); //Transaction
		LOG.setContext(MdcType.CORRELATION_ID, correlationId); //Correlation
		LOG.debug("Início da Validação do Conta");
		LOG.info(record.value(), MessageText.EVENT_RECEIVED);
		
		//Span span = spanDatastore.getCurrentSpan();
		Span span = kafka.startConsumerSpan("ValidaConta", headers, tracer);
		
		span.setTag("evento",transferencia.getEvento());
		

		boolean aprovadoOrigem = true;
		boolean aprovadoDestino = true;
		String razaoOrigem = "[0] Conta Origem verificada e saldo suficiente";
		String razaoDestino = "[0] Conta Destino verificada";
		
		ContaCorrenteDto contaDestino=null;
		ContaCorrenteDto contaOrigem=null;

		//verificando conta origem
		try
		{
			span.log("Vetrificando a conta origem!");
			contaOrigem = repConta.pesquisaPorAgenciaConta(transferencia.getAgenciaOrigem(), transferencia.getContaOrigem(), transferencia.getDvOrigem()).get(0);
			LOG.debug(MessagesEnum.GM4C_CTA0002D.getCodAndDescription(), contaOrigem);
		}
		catch (Exception e)
		{
			span.log("Conta Origem nao encontrada!");
			LOG.error(MessagesEnum.GM4C_CTA0001E.getCodAndDescription());
			span.setTag("error", true);
			aprovadoOrigem = false;
		}
		//verificando conta destino
		try
		{
			span.log("Verificando conta destino");
			contaDestino = repConta.pesquisaPorAgenciaConta(transferencia.getAgenciaDestino(), transferencia.getContaDestino(), transferencia.getDvDestino()).get(0);
			LOG.debug(MessagesEnum.GM4C_CTA0003D.getCodAndDescription(), contaDestino);
		}
		catch (Exception e)
		{
			span.log("Conta Destino nao encontrada!");
			LOG.error(MessagesEnum.GM4C_CTA0002E.getCodAndDescription(), e.getMessage());
			span.setTag("error", true);
			aprovadoDestino = false;
		}

		if (transferencia.getEvento().equalsIgnoreCase("efetivacao"))
		{		
			if (contaOrigem!=null && contaDestino!=null)
			{
				contaOrigem.setValor_saldo(contaOrigem.getValor_saldo()-transferencia.getValor());
				contaDestino.setValor_saldo(contaDestino.getValor_saldo()+transferencia.getValor());
//				repConta.save(contaOrigem);
				if (syntheticTransaction == true) {
					LOG.warn(MessageText.SYNTHETIC_TRANSACTION);
				} else {
					span.log("Atualizando o saldo da conta origem!");
					repConta.save(contaOrigem);
				}
				LOG.info(MessagesEnum.GM4C_CTA0002I.getCodAndDescription());
//				repConta.save(contaDestino);
				if (syntheticTransaction == true) {
					LOG.warn(MessageText.SYNTHETIC_TRANSACTION);
				} else {
					span.log("Atualizando o saldo da conta destino");
					repConta.save(contaDestino);
				}
				LOG.info(MessagesEnum.GM4C_CTA0003I.getCodAndDescription());
				razaoOrigem = "[0] Debito realizado com sucesso na conta origem!";
				razaoDestino = "[0] Credito realizado com sucesso na conta destino";
				span.log(razaoOrigem);
				span.log(razaoDestino);
			}
			contadorAppMessageSubscribeEfetivacao.increment();
			LOG.debug(MessagesEnum.GM4C_CTA0005D.getCodAndDescription(), "contadorAppMessageSubscribeEfetivacao");
			
			
		}
		else //simulacao
		{
			
			if (contaOrigem ==null) 
			{
				aprovadoOrigem=false;
				razaoOrigem = "[-10] Conta Origem nao existe";
				LOG.error(MessagesEnum.GM4C_CTA0003E.getCodAndDescription());
				span.log(razaoOrigem);
				span.setTag("error", true);
			}
			else if (contaOrigem.getBloqueio()==1)
			{
				aprovadoOrigem=false;
				razaoOrigem = "[-11] Conta Origem bloqueada";
				LOG.error(MessagesEnum.GM4C_CTA0004E.getCodAndDescription());
				span.log(razaoOrigem);
				span.setTag("error", true);

			}
			else if (contaOrigem.getValor_saldo()<transferencia.getValor())
			{
				aprovadoOrigem=false;
				razaoOrigem = "[-12] Saldo Insuficiente";
				LOG.info(MessagesEnum.GM4C_CTA0001I.getCodAndDescription());
				span.log(razaoOrigem);
				span.setTag("error", true);

			}
			
			if (contaDestino == null) 
			{
				aprovadoDestino=false;
				razaoDestino = "[-10] Conta Destino nao existe";
				LOG.error(MessagesEnum.GM4C_CTA0005E.getCodAndDescription());
				span.log(razaoDestino);
				span.setTag("error", true);

			}
			else if (contaDestino.getBloqueio()==1 )
			{
				aprovadoDestino=false;
				razaoDestino = "[-11] Conta Destino bloqueada";
				LOG.error(MessagesEnum.GM4C_CTA0006E.getCodAndDescription());
				span.log(razaoDestino);
				span.setTag("error", true);

			}
			
			
			contadorAppMessageSubscribeSimulacao.increment();
			LOG.debug(MessagesEnum.GM4C_CTA0005D.getCodAndDescription(), "contadorAppMessageSubscribeSimulacao");
		}
		
		LOG.debug("Validação Conta Origem {}", aprovadoOrigem);
		LOG.debug("Validação Conta Destino {}", aprovadoDestino);

		span.log("preparando mensagem Avro com a resposta da validação das contas");
		//prepara o registro do avro sobre o retorno das contas
		ContaCorrente conta = ContaCorrente.newBuilder()
				.setAgenciaOrigem(transferencia.getAgenciaOrigem())
				.setContaOrigem(transferencia.getContaOrigem())
				.setDvOrigem(transferencia.getDvOrigem())
				.setAprovacaoContaOrigem(aprovadoOrigem)
				.setMotivoContaOrigem(razaoOrigem)
				.setAgenciaDestino(transferencia.getAgenciaDestino())
				.setContaDestino(transferencia.getContaDestino())
				.setDvDestino(transferencia.getDvDestino())
				.setAprovacaoContaDestino(aprovadoDestino)
				.setMotivoContaDestino(razaoDestino)
				.setIdSimulacao(transferencia.getIdTransacao())
				.setEvento(transferencia.getEvento())
				.build();
		LOG.info(conta, MessagesEnum.GM4C_CTA0004I.getCodAndDescription());		
		//envia a respota do limite para o kafka no topico conta
		//kafkaConta.send("conta", conta);
		
		kafka.sendMessage(conta, span, "conta", tracer, kafkaConta, "EnviaRespostaConta", correlationId, transactionId, syntheticTransaction);
		LOG.info(conta, MessageText.EVENT_PRODUCED);
		span.log("mensagem e enviada ao topico conta do kafka!");
		String resultado = "SUCESSO";
		if (!aprovadoDestino || !aprovadoOrigem)
		{
			resultado = "ERRO";
		}
		Metrics.counter("app.message.publish", "app", "conta", "fluxo", transferencia.getEvento(), "topico", "conta","resultado",resultado).increment();
		OffsetDateTime dataHoraFinal = OffsetDateTime.now();
		long diferencaTempo = Duration.between(dataHoraInicial, dataHoraFinal).toMillis();
		summary.record( diferencaTempo );
		span.finish();
		LOG.debug("Fim da Validação do Conta");
		LOG.clearContext();
	}

}
