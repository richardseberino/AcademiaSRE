package com.gm4c.senha;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.record.TimestampType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHeaders;

import com.gm4c.senha.dto.SenhaDto;
import com.gm4c.senha.dto.SenhaRepositorio;
import com.gm4c.senha.kafka.KafkaService;
import com.gm4c.tef.Transferencia;
import com.gm4c.utils.KafkaHeaderMap;

import io.micrometer.core.instrument.Counter;
import io.opentracing.References;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;

@SpringBootTest
public class Gm4cSenhaServiceTest {

	@Mock
	@Autowired
	private SenhaRepositorio repSenha;
	
	@Mock
	@Autowired
	private Counter contadorSenhaCorreta;
	
	@Mock
	@Autowired
	private Counter contadorSenhaIncorreta;
	
	@Mock
	@Autowired
	private KafkaService kafka;
	
	@Mock
	private Span span;
	
	@InjectMocks
	private Gm4cSenhaService senhaService;
	
	@Autowired
	private Tracer tracer;
	
	@Autowired
	private KafkaTemplate<String, Senha> senhaTemplate;
	
	public Consumer<String, Senha> consumer;

	@Autowired
	public ConsumerFactory<String, Senha> consumerFactory;

	@Autowired
	public String idTransacao = UUID.randomUUID().toString();

	@Autowired
	public String idCorrelacao = UUID.randomUUID().toString();

	@Autowired
	public Boolean synstheticTransaction = false;
	
	@BeforeEach
	public void before() {
//		senhaService = new SenhaService(senhaTemplate, tracer);
		senhaService = new Gm4cSenhaService(senhaTemplate);
		consumer = consumerFactory.createConsumer();
		consumer.subscribe(Collections.singletonList("senha"));
		
		TextMap carrier = new KafkaHeaderMap();
        SpanContext parent = tracer.extract(Format.Builtin.TEXT_MAP, carrier);
        this.span = tracer.buildSpan("Valida Senha") //
                .addReference(References.FOLLOWS_FROM, parent) //
                .start();
        
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void simulacaoValidadaTest() {
		Transferencia transferencia = new Transferencia("simulacao", 3802, 339484, 3, 3394, 874433, 2, (float) 10.00, "senha", UUID.randomUUID().toString(), "CC_CC");
		
		ConsumerRecord<String, Transferencia> senhaRecord = new ConsumerRecord<String, Transferencia>("teste", 1, 1, 1, TimestampType.CREATE_TIME, 1, 1, 1, "transacao", transferencia);
		
		Map<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("transacao", senhaRecord.headers().headers("transacao"));
		MessageHeaders headers = new MessageHeaders(headerMap);
		
		SenhaDto senha = new SenhaDto();
		senha.setAgencia(3802);
		senha.setConta(339484);
		senha.setDv(3);
		senha.setSenha("senha");
		
		List<SenhaDto> listSenha = new ArrayList<SenhaDto>();
		listSenha.add(senha);
		
		TextMap carrier = new KafkaHeaderMap(headers);
        SpanContext parent = tracer.extract(Format.Builtin.TEXT_MAP, carrier);
        Span span = tracer.buildSpan("Valida Senha") //
                .addReference(References.FOLLOWS_FROM, parent) //
                .start();
		
		Mockito.doReturn(span).when(kafka).startConsumerSpan(Mockito.anyString(), Mockito.any(MessageHeaders.class), Mockito.any(Tracer.class));
		Mockito.doReturn(listSenha).when(repSenha).pesquisaPorAgenciaConta(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
		Mockito.doNothing().when(contadorSenhaCorreta).increment();
		Mockito.doNothing().when(kafka).sendMessage(Mockito.any(Senha.class), Mockito.any(Span.class), Mockito.anyString(), Mockito.any(Tracer.class), Mockito.any(KafkaTemplate.class), Mockito.anyString(), idCorrelacao, idTransacao, synstheticTransaction);
		
		ArgumentCaptor<Senha> capturaSenha = ArgumentCaptor.forClass(Senha.class);
		
		senhaService.validaSenha(senhaRecord, headers);
		
		verify(kafka).sendMessage(capturaSenha.capture(), Mockito.any(Span.class), Mockito.anyString(), Mockito.any(Tracer.class), Mockito.any(KafkaTemplate.class), Mockito.anyString(), idCorrelacao, idTransacao, synstheticTransaction);
		
		Senha senhaResposta = capturaSenha.getValue();
		assertTrue(senhaResposta.getAprovado());
		assertEquals(transferencia.getIdTransacao(), senhaResposta.getIdSimulacao());
		assertEquals(transferencia.getEvento(), senhaResposta.getEvento());
	}
	
	@Test
	public void simulacaoInvalidaTest() {
		Transferencia transferencia = new Transferencia("simulacao", 3802, 339484, 3, 3394, 874433, 2, (float) 10.00, "1234", UUID.randomUUID().toString(), "CC_CC");
		
		ConsumerRecord<String, Transferencia> senhaRecord = new ConsumerRecord<String, Transferencia>("teste", 1, 1, 1, TimestampType.CREATE_TIME, 1, 1, 1, "transacao", transferencia);
		
		Map<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("transacao", senhaRecord.headers().headers("transacao"));
		MessageHeaders headers = new MessageHeaders(headerMap);
		
		SenhaDto senha = new SenhaDto();
		senha.setAgencia(3802);
		senha.setConta(339484);
		senha.setDv(3);
		senha.setSenha("senha");
		
		List<SenhaDto> listSenha = new ArrayList<SenhaDto>();
		listSenha.add(senha);
		
		TextMap carrier = new KafkaHeaderMap(headers);
        SpanContext parent = tracer.extract(Format.Builtin.TEXT_MAP, carrier);
        Span span = tracer.buildSpan("Valida Senha") //
                .addReference(References.FOLLOWS_FROM, parent) //
                .start();
		
		Mockito.doReturn(span).when(kafka).startConsumerSpan(Mockito.anyString(), Mockito.any(MessageHeaders.class), Mockito.any(Tracer.class));
		Mockito.doReturn(listSenha).when(repSenha).pesquisaPorAgenciaConta(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
		Mockito.doNothing().when(contadorSenhaIncorreta).increment();
		Mockito.doNothing().when(kafka).sendMessage(Mockito.any(Senha.class), Mockito.any(Span.class), Mockito.anyString(), Mockito.any(Tracer.class), Mockito.any(KafkaTemplate.class), Mockito.anyString(), idCorrelacao, idTransacao, synstheticTransaction);
		
		ArgumentCaptor<Senha> capturaSenha = ArgumentCaptor.forClass(Senha.class);
		
		senhaService.validaSenha(senhaRecord, headers);
		
		verify(kafka).sendMessage(capturaSenha.capture(), Mockito.any(Span.class), Mockito.anyString(), Mockito.any(Tracer.class), Mockito.any(KafkaTemplate.class), Mockito.anyString(), idCorrelacao, idTransacao, synstheticTransaction);
		
		Senha senhaResposta = capturaSenha.getValue();
		assertFalse(senhaResposta.getAprovado());
		assertEquals(transferencia.getIdTransacao(), senhaResposta.getIdSimulacao());
		assertEquals(transferencia.getEvento(), senhaResposta.getEvento());
	}
	
	@Test
	public void simulacaoSenhaNaoCadastradaTest() {
		Transferencia transferencia = new Transferencia("simulacao", 3802, 339484, 3, 3394, 874433, 2, (float) 10.00, "1234", UUID.randomUUID().toString(), "CC_CC");
		
		ConsumerRecord<String, Transferencia> senhaRecord = new ConsumerRecord<String, Transferencia>("teste", 1, 1, 1, TimestampType.CREATE_TIME, 1, 1, 1, "transacao", transferencia);
		
		Map<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("transacao", senhaRecord.headers().headers("transacao"));
		MessageHeaders headers = new MessageHeaders(headerMap);
		
		SenhaDto senha = new SenhaDto();
		senha.setAgencia(3802);
		senha.setConta(339484);
		senha.setDv(3);
		senha.setSenha("senha");
		
		List<SenhaDto> listSenha = new ArrayList<SenhaDto>();
		
		TextMap carrier = new KafkaHeaderMap(headers);
        SpanContext parent = tracer.extract(Format.Builtin.TEXT_MAP, carrier);
        Span span = tracer.buildSpan("Valida Senha") //
                .addReference(References.FOLLOWS_FROM, parent) //
                .start();
		
		Mockito.doReturn(span).when(kafka).startConsumerSpan(Mockito.anyString(), Mockito.any(MessageHeaders.class), Mockito.any(Tracer.class));
		Mockito.doReturn(listSenha).when(repSenha).pesquisaPorAgenciaConta(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
		Mockito.doNothing().when(contadorSenhaIncorreta).increment();
		Mockito.doNothing().when(kafka).sendMessage(Mockito.any(Senha.class), Mockito.any(Span.class), Mockito.anyString(), Mockito.any(Tracer.class), Mockito.any(KafkaTemplate.class), Mockito.anyString(), idCorrelacao, idTransacao, synstheticTransaction);
		
		ArgumentCaptor<Senha> capturaSenha = ArgumentCaptor.forClass(Senha.class);
		
		senhaService.validaSenha(senhaRecord, headers);
		
		verify(kafka).sendMessage(capturaSenha.capture(), Mockito.any(Span.class), Mockito.anyString(), Mockito.any(Tracer.class), Mockito.any(KafkaTemplate.class), Mockito.anyString(), idCorrelacao, idTransacao, synstheticTransaction);
		
		Senha senhaResposta = capturaSenha.getValue();
		assertFalse(senhaResposta.getAprovado());
		assertEquals(transferencia.getIdTransacao(), senhaResposta.getIdSimulacao());
		assertEquals(transferencia.getEvento(), senhaResposta.getEvento());
	}
	
	@Test
	public void efetivacaoSenhaTest() {
		Transferencia transferencia = new Transferencia("efetivacao", 3802, 339484, 3, 3394, 874433, 2, (float) 10.00, "1234", UUID.randomUUID().toString(), "CC_CC");
		
		ConsumerRecord<String, Transferencia> senhaRecord = new ConsumerRecord<String, Transferencia>("teste", 1, 1, 1, TimestampType.CREATE_TIME, 1, 1, 1, "transacao", transferencia);
		
		Map<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("transacao", senhaRecord.headers().headers("transacao"));
		MessageHeaders headers = new MessageHeaders(headerMap);
		
		TextMap carrier = new KafkaHeaderMap(headers);
        SpanContext parent = tracer.extract(Format.Builtin.TEXT_MAP, carrier);
        Span span = tracer.buildSpan("Valida Senha") //
                .addReference(References.FOLLOWS_FROM, parent) //
                .start();
		
        
		Mockito.doReturn(this.span).when(kafka).startConsumerSpan(Mockito.anyString(), Mockito.any(MessageHeaders.class), Mockito.any(Tracer.class));
		Mockito.doReturn(span).when(this.span).log(Mockito.anyString());
		
		ArgumentCaptor<String> spanLog = ArgumentCaptor.forClass(String.class);
		
		senhaService.validaSenha(senhaRecord, headers);
		
		verify(this.span).log(spanLog.capture());
		
		assertEquals("Evento de efetivação, senha nao precisa ser validada, ignorando mensagem", spanLog.getValue());
		
	}
}
