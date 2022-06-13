package com.gm4c.tef.healthcheck;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.gm4c.tef.Transferencia;
import com.gm4c.tef.kafka.KafkaService;

import io.opentracing.Span;
import io.opentracing.Tracer;


@Component
@ConditionalOnProperty(name = "management.health.senha.enabled", havingValue = "true", matchIfMissing = true)
public class SenhaHealthIndicator extends AbstractHealthIndicator {

	@Value("${management.health.senha.enabled}")
	private Boolean hcEnabled;

	@Value("${management.health.senha.level}")
	private long hcSevel;

	@Value("${management.health.senha.url}")
	private String hcUrl;
		
	@Value("${management.health.senha.threshold:500}")
	private Integer hcThreshold;

	@Value("${management.health.senha.topic-req}")
	private String hcTopic;

	@Autowired
	private Tracer tracer;
	
	@Autowired
	private final KafkaTemplate<String, Transferencia> kafkaSimulacao;// = new KafkaProducer<String, Simulacao>(propriedades);
	
	private KafkaService kafka = new KafkaService();
	
	public SenhaHealthIndicator (KafkaTemplate<String, Transferencia> kafka)
	{
	
		this.kafkaSimulacao = kafka;	
	}

	private String correlationId;
	private String transactionId;
	private Boolean syntheticTransaction = true; 

	protected void doHealthCheck(Health.Builder builder) throws Exception {

		String topico = hcTopic;
		
		if ( hcEnabled == true ) {
			long start = System.currentTimeMillis();
			Date  date = new Date();
			Timestamp startTimestamp = new Timestamp(date.getTime());
			long finish;
			long timeElapsed;
	
			if ( hcSevel >= 2 ) {

				try {
	
					String url =  hcUrl + "/actuator/health";
					RestTemplate restTemplate = new RestTemplate();
					ResponseEntity<String> response
					  = restTemplate.getForEntity(url, String.class);
					assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
	
					finish = System.currentTimeMillis();
					date = new Date();
					Timestamp endTimestamp = new Timestamp(date.getTime());
					timeElapsed = finish - start;
				
					builder.up().withDetail("L2-healthCheckLevel", hcSevel);
					builder.up().withDetail("L2-status", "PASS");
					builder.up().withDetail("L2-product", "Senha");
					builder.up().withDetail("L2-Timestamp Start", startTimestamp.toString());
					builder.up().withDetail("L2-Timestamp End", endTimestamp.toString());
					builder.up().withDetail("L2-elapsedTime (ms)", timeElapsed);
					builder.up().withDetail("L2-threshold (ms)", hcThreshold);
					builder.up().withDetail("L2-payload", response);
				}
		
				catch (Exception ex) {
					finish = System.currentTimeMillis();
					date = new Date();
					Timestamp endTimestamp = new Timestamp(date.getTime());
					timeElapsed = finish - start;

					builder.down().withDetail("L2-healthCheckLevel", hcSevel);
					builder.down().withDetail("L2-status", "NOT PASS");
					builder.down().withDetail("L2-product", "Senha");
					builder.down().withDetail("L2-startTimestamp", startTimestamp);
					builder.down().withDetail("L2-endTimestamp", endTimestamp);
					builder.down().withDetail("L2-elapsedTime (ms)", timeElapsed); 
					builder.down().withDetail("L2-threshold (ms)", hcThreshold);
					builder.down().withDetail("L2-messageStatus", ex.getLocalizedMessage()); 
				}
			}

			if ( hcSevel >= 3 ) {
				transactionId = UUID.randomUUID().toString();
				correlationId = UUID.randomUUID().toString();
				Transferencia transferencia = Transferencia.newBuilder()
					.setEvento("simulacao")
					.setAgenciaOrigem(0)
					.setContaOrigem(0)
					.setDvOrigem(0)
					.setAgenciaDestino(0)
					.setContaDestino(0)
					.setDvDestino(0)
					.setValor(0)
					.setTipoTransacao("")
					.setSenha("0")
					.setIdTransacao(transactionId)
					.build(); 

				Span span = this.startServerSpan("HealthCheck", transferencia);  //tracer.buildSpan("TEF-efetivacao").start();
				
				try {
					
					kafka.sendMessage(transferencia, span, topico, tracer, kafkaSimulacao, "EnviaEventoSimulacaoTef", correlationId, transactionId, syntheticTransaction);
	
					finish = System.currentTimeMillis();
					date = new Date();
					Timestamp endTimestamp = new Timestamp(date.getTime());
					timeElapsed = finish - start;
	
					builder.up().withDetail("L3-healthCheckLevel", hcSevel);
					builder.up().withDetail("L3-status", "PASS");
					builder.up().withDetail("L3-product", "Senha-Dep-Kafka-SyntheticTransaction");
					builder.up().withDetail("L3-Timestamp Start", startTimestamp.toString());
					builder.up().withDetail("L3-Timestamp End", endTimestamp.toString());
					builder.up().withDetail("L3-elapsedTime (ms)", timeElapsed);
					builder.up().withDetail("L3-threshold (ms)", hcThreshold);
					builder.up().withDetail("L3-topic-req", hcTopic);
				}
				catch (Exception ex) {
					finish = System.currentTimeMillis();
					date = new Date();
					Timestamp endTimestamp = new Timestamp(date.getTime());
					timeElapsed = finish - start;

					builder.down().withDetail("L3-status", "PASS");
					builder.down().withDetail("L3-product", "Senha-Dep-Kafka-SyntheticTransaction");
					builder.down().withDetail("L3-startTimestamp", startTimestamp);
					builder.down().withDetail("L3-endTimestamp", endTimestamp);
					builder.down().withDetail("L3-elapsedTime (ms)", timeElapsed); 
					builder.down().withDetail("L3-threshold (ms)", hcThreshold);
					builder.down().withDetail("L3-messageStatus", ex.getLocalizedMessage()); 
					builder.down().withDetail("L3-topic-req", hcTopic);
				}
				span.finish();
			}
			return;
		}
	}

	private Span startServerSpan(String string, Transferencia transferencia) {
		// TODO Auto-generated method stub
		return null;
	}
}