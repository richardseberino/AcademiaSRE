package com.gm4c.tef.healthcheck;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.gm4c.tef.Transferencia;
import com.gm4c.tef.kafka.KafkaService;
import com.gm4c.tef.repository.DependenciesHealthIndicatorRepository;

import io.opentracing.Span;
import io.opentracing.Tracer;

@Qualifier("healthcustom")
@Component
public class DependenciesHealthIndicator extends AbstractHealthIndicator {

	private Boolean hcEnabled;

	private long hcLevel;

	private String hcUrl;
		
	private long hcThreshold;

	private String hcTopic;

	private String hcName;

	@Autowired
	private Tracer tracer;
	
	@Autowired
	private final KafkaTemplate<String, Transferencia> kafkaSimulacao;// = new KafkaProducer<String, Simulacao>(propriedades);
	
	private KafkaService kafka = new KafkaService();
	
    private List<DependenciesHealthIndicatorRepository> deps;

	@Autowired
	public DependenciesHealthIndicator (KafkaTemplate<String, Transferencia> kafka, HealthDependencies dep) 
	{
		this.kafkaSimulacao = kafka;
		this.deps = dep.getDependencies();
	}
	
	private String correlationId;
	private String transactionId;
	private Boolean syntheticTransaction = true; 

	protected void doHealthCheck(Health.Builder builder) throws Exception {

		String topico = hcTopic;

		for (int i = 0; i < deps.size(); i++) {
			
			DependenciesHealthIndicatorRepository dep = deps.get(i);

			hcName = dep.getName(); 
			hcEnabled = dep.isEnabled();
			hcLevel = dep.getLevel();
			hcThreshold = dep.getThreshold();
			hcTopic = dep.getTopicreq();
			hcUrl = dep.getUrl();
				
			if ( hcEnabled == true ) {
				long start = System.currentTimeMillis();
				Date  date = new Date();
				Timestamp startTimestamp = new Timestamp(date.getTime());
				long finish;
				long timeElapsed;
		
				if ( hcLevel >= 2 ) {
	
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
				
						builder.up().withDetail(hcName+"-"+"L2-healthCheckLevel", hcLevel);
						builder.up().withDetail(hcName+"-"+"L2-status", "PASS");
						builder.up().withDetail(hcName+"-"+"L2-product", hcName);
						builder.up().withDetail(hcName+"-"+"L2-Timestamp Start", startTimestamp.toString());
						builder.up().withDetail(hcName+"-"+"L2-Timestamp End", endTimestamp.toString());
						builder.up().withDetail(hcName+"-"+"L2-elapsedTime (ms)", timeElapsed);
						builder.up().withDetail(hcName+"-"+"L2-threshold (ms)", hcThreshold);
						builder.up().withDetail(hcName+"-"+"L2-payload", response);
					}
		
					catch (Exception ex) {
						finish = System.currentTimeMillis();
						date = new Date();
						Timestamp endTimestamp = new Timestamp(date.getTime());
						timeElapsed = finish - start;

//						builder.down().withDetail(hc_dep_name+"-"+"L2-healthCheckLevel", hc_dep_level);
//						builder.down().withDetail(hc_dep_name+"-"+"L2-status", "NOT PASS");
//						builder.down().withDetail(hc_dep_name+"-"+"L2-product", hc_dep_name);
//						builder.down().withDetail(hc_dep_name+"-"+"L2-startTimestamp", startTimestamp);
//						builder.down().withDetail(hc_dep_name+"-"+"L2-endTimestamp", endTimestamp);
//						builder.down().withDetail(hc_dep_name+"-"+"L2-elapsedTime (ms)", timeElapsed); 
//						builder.down().withDetail(hc_dep_name+"-"+"L2-threshold (ms)", hc_dep_threshold);
//						builder.down().withDetail(hc_dep_name+"-"+"L2-messageStatus", ex.getLocalizedMessage()); 
						builder.up().withDetail(hcName+"-"+"L2-healthCheckLevel", hcLevel);
						builder.up().withDetail(hcName+"-"+"L2-status", "NOT PASS");
						builder.up().withDetail(hcName+"-"+"L2-product", hcName);
						builder.up().withDetail(hcName+"-"+"L2-startTimestamp", startTimestamp);
						builder.up().withDetail(hcName+"-"+"L2-endTimestamp", endTimestamp);
						builder.up().withDetail(hcName+"-"+"L2-elapsedTime (ms)", timeElapsed); 
						builder.up().withDetail(hcName+"-"+"L2-threshold (ms)", hcThreshold);
						builder.up().withDetail(hcName+"-"+"L2-messageStatus", ex.getLocalizedMessage()); 
					}
				}

				if ( hcLevel >= 3 ) {
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

					//Span span = this.startServerSpan("HealthCheck", transferencia);  //tracer.buildSpan("TEF-efetivacao").start();
					
					try {
					
						kafka.sendMessage(transferencia, null, topico, tracer, kafkaSimulacao, "EnviaEventoSimulacaoTef-Sintetico", correlationId, transactionId, syntheticTransaction);
	
						finish = System.currentTimeMillis();
						date = new Date();
						Timestamp endTimestamp = new Timestamp(date.getTime());
						timeElapsed = finish - start;
	
						builder.up().withDetail(hcName+"-"+"L3-healthCheckLevel", hcLevel);
						builder.up().withDetail(hcName+"-"+"L3-status", "PASS");
						builder.up().withDetail(hcName+"-"+"L3-product", hcName + "-Dep-Kafka-SyntheticTransaction");
						builder.up().withDetail(hcName+"-"+"L3-Timestamp Start", startTimestamp.toString());
						builder.up().withDetail(hcName+"-"+"L3-Timestamp End", endTimestamp.toString());
						builder.up().withDetail(hcName+"-"+"L3-elapsedTime (ms)", timeElapsed);
						builder.up().withDetail(hcName+"-"+"L3-threshold (ms)", hcThreshold);
						builder.up().withDetail(hcName+"-"+"L3-topic-req", hcTopic);
					}
				
					catch (Exception ex) {
						finish = System.currentTimeMillis();
						date = new Date();
						Timestamp endTimestamp = new Timestamp(date.getTime());
						timeElapsed = finish - start;

//						builder.down().withDetail(hc_dep_name+"-"+"L3-status", "PASS");
//						builder.down().withDetail(hc_dep_name+"-"+"L3-product", hc_dep_name+"-Dep-Kafka-SyntheticTransaction");
//						builder.down().withDetail(hc_dep_name+"-"+"L3-startTimestamp", startTimestamp);
//						builder.down().withDetail(hc_dep_name+"-"+"L3-endTimestamp", endTimestamp);
//						builder.down().withDetail(hc_dep_name+"-"+"L3-elapsedTime (ms)", timeElapsed); 
//						builder.down().withDetail(hc_dep_name+"-"+"L3-threshold (ms)", hc_dep_threshold);
//						builder.down().withDetail(hc_dep_name+"-"+"L3-messageStatus", ex.getLocalizedMessage()); 
//						builder.down().withDetail(hc_dep_name+"-"+"L3-topic-req", hc_dep_topic_req);
						builder.up().withDetail(hcName+"-"+"L3-status", "PASS");
						builder.up().withDetail(hcName+"-"+"L3-product", hcName+"-Dep-Kafka-SyntheticTransaction");
						builder.up().withDetail(hcName+"-"+"L3-startTimestamp", startTimestamp);
						builder.up().withDetail(hcName+"-"+"L3-endTimestamp", endTimestamp);
						builder.up().withDetail(hcName+"-"+"L3-elapsedTime (ms)", timeElapsed); 
						builder.up().withDetail(hcName+"-"+"L3-threshold (ms)", hcThreshold);
						builder.up().withDetail(hcName+"-"+"L3-messageStatus", ex.getLocalizedMessage()); 
						builder.up().withDetail(hcName+"-"+"L3-topic-req", hcTopic);
					}
					//span.finish();
				}
			}
		}
		return;
	}

	//private Span startServerSpan(String string, Transferencia transferencia) {
		// TODO Auto-generated method stub
	//	return null;
	//}

}