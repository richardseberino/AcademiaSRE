package com.gm4c.tef;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.gm4c.conta.ContaCorrente;
import com.gm4c.limite.Limite;
import com.gm4c.logging.factories.ContextLoggerFactory;
import com.gm4c.logging.ports.IContextLogger;
import com.gm4c.logging.types.MdcType;
import com.gm4c.logging.types.MessageText;
import com.gm4c.senha.Senha;
import com.gm4c.tef.domain.MessagesEnum;
import com.gm4c.tef.dto.RequestEfetivacaoTefDto;
import com.gm4c.tef.dto.RequestSimulacaoTefDto;
import com.gm4c.tef.dto.ResultadoSimulacaoTefDto;
import com.gm4c.tef.dto.TefDto;
import com.gm4c.tef.dto.TracerLabels;
import com.gm4c.tef.kafka.KafkaService;
import com.gm4c.tef.repository.TefRepository;
import com.google.gson.Gson;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Metrics;
import io.opentracing.Span;
import io.opentracing.Tracer;

@RestController
@Component
@RequestMapping("/tef")
public class Gm4cTefController extends com.gm4c.trace.ProgragacaoContextoTrace {

	
	@Autowired
	private final KafkaTemplate<String, Transferencia> kafkaSimulacao;// = new KafkaProducer<String, Simulacao>(propriedades);

    private KafkaService kafka = new KafkaService();
    
    @Autowired
    private Tracer tracer;

    @Autowired
	private TefRepository repTef;
	
	private static final IContextLogger LOG = ContextLoggerFactory.getLogger(Gm4cTefController.class);
				
	public Gm4cTefController(KafkaTemplate<String, Transferencia> kafka)
	{

		this.kafkaSimulacao = kafka;	}
	
	@PatchMapping("/efetivacao")
	@Timed( value = "app.request.duration", extraTags = { "evento", "efetivacao" } )
	public ResponseEntity<ResultadoSimulacaoTefDto> efetivaTransferencia(@RequestBody RequestEfetivacaoTefDto efetivacao, HttpServletRequest request, @RequestHeader HttpHeaders headers)
	{

		String correlationId = UUID.randomUUID().toString();
		String transactionId = efetivacao.getIdTransacao();
		boolean syntheticTransaction=false;
		boolean error=false;
		
		Map<String, Object> returnValue = new HashMap<>();

		Enumeration<String> hearderNames = request.getHeaderNames();
		while(hearderNames.hasMoreElements())
	    {
		     String headerName = hearderNames.nextElement();
		     returnValue.put(headerName, request.getHeader(headerName));   
		 }
	     
		Iterator<String> keys = returnValue.keySet().iterator();
		while (keys.hasNext())
		{
			String key = keys.next();
			Object value = returnValue.get(key);
	        if (key.equalsIgnoreCase("correlationId"))
	        {
		        	correlationId = (String) value;
	        }
	        else if (key.equalsIgnoreCase("syntheticTransaction"))
	        {
	        	if (value.toString().contentEquals("true"))
	        	{
	        		syntheticTransaction = true;
	        	}
	        }

		}
		
	    
	    LOG.setContext(MdcType.CORRELATION_ID, correlationId); //Correlation	    
		LOG.setContext(MdcType.TRANSACTION_ID, transactionId); //Transaction
		
		LOG.debug("Início da efetivação - Method: efetivaTransferencia - efetivacao: {} - request: {}, headers: {}", efetivacao,request,headers);	
		LOG.debug("TransactionId {} definido", transactionId);
		LOG.info(efetivacao, MessageText.REQUEST_RECEIVED_TRANS, "Efetivação");
        
		if (syntheticTransaction == true) {
			//LOG.warn(MessageText.SYNTHETIC_TRANSACTION);
			ResultadoSimulacaoTefDto resultado = new ResultadoSimulacaoTefDto();
			return ResponseEntity.ok(resultado);		
		}

        //define o topico kafka
		String topico = "tef";
		LOG.debug("Topico {} definido",topico);
		
		//Span span = spanDatastore.getCurrentSpan();
		Span span = this.startServerSpan("TEF-efetivacao", request);  //tracer.buildSpan("TEF-efetivacao").start();
		span.setTag("transaction_id", transactionId);
		span.setTag("correlationId", correlationId);
		
		//Recupera os dados da simulacao
		span.log("Vai procurar os dados da Simulacao no Redis");
		Optional<TefDto> t1= repTef.findById(efetivacao.getIdTransacao()); 
		LOG.info(MessagesEnum.GM4C_TEF0014I.getCodAndDescription());
		
		//verifica se existe uma simulacao feita com esse id
		if (!t1.isPresent())
		{
			span.log("Simulacao nao encontrada na base Redis");
			LOG.info(MessagesEnum.GM4C_TEF0001I.getCodAndDescription());
			span.setTag("error", true);
			span.finish();
			return ResponseEntity.notFound().build();
		}
		
		
		TefDto simulacao = t1.get();
		if (!simulacao.getRc_efetivacao().startsWith("[2]") || !simulacao.getRc_simulacao().startsWith("[0]"))
		{
			span.setTag("error", true);
			span.log("Simulacao já foi efetivada ou Simulacao não terminou com sucesso!");
			LOG.info(MessagesEnum.GM4C_TEF0001E.getCodAndDescription());	
			span.finish();
			return ResponseEntity.status(403).build();
		}
		
		span.setTag("rest.url", "/tef/efetivacao");
		
		simulacao.setEvento("efetivacao");
		simulacao.setRc_credito("[2] pendente");
		simulacao.setRc_debito("[2] pendente");
		simulacao.setRc_efetivacao("[2] enviada");
		simulacao.setRc_limite("[2] pendente");
		span.log("Vai salvar o inicio da efetivacao na base Redis");
		repTef.save(simulacao);
		span.log("Efetivacao pendente salva na base do Redis, criando mensagem Avro");
		//cria o objeto do Avro com a mensagem do evento de efetiva ao
		Transferencia efetivaAvro = Transferencia.newBuilder()
				.setEvento("efetivacao")
				.setAgenciaOrigem(simulacao.getAgencia_origem())
				.setContaOrigem(simulacao.getConta_origem())
				.setDvOrigem(simulacao.getDv_origem())
				.setAgenciaDestino(simulacao.getAgencia_destino())
				.setContaDestino(simulacao.getConta_destino())
				.setDvDestino(simulacao.getDv_destino())
				.setValor(simulacao.getValor())
				.setTipoTransacao(simulacao.getTipo())
				.setSenha(simulacao.getSenha())
				.setIdTransacao(simulacao.getId())
				.build(); 
		LOG.info(efetivaAvro, MessagesEnum.GM4C_TEF0007I.getCodAndDescription());		
		span.log("Mensagem avro criada para iniciar a efetivacao");
		//envia a mensagem ao kafka
		//kafkaSimulacao.send(topico, efetivaAvro);
		kafka.sendMessage(efetivaAvro, span, topico, tracer, kafkaSimulacao, "EnviaEventoEfetivacaoTef", correlationId, transactionId, syntheticTransaction);
		Metrics.counter("app.message.publish", "app", "tef", "fluxo", "efetivacao", "topico", "tef").increment();
		LOG.info(efetivaAvro, MessageText.EVENT_PRODUCED);
		span.log("Mensagem Avro enviada ao topico TEF do Kafka");
		
		Date inicio = new Date();
		Date agora = new Date();
		try
		{
			while (!verificaEtapa(transactionId, "efetivacao")) 
			{
				agora = new Date();
				if (agora.getTime()-inicio.getTime()>2000)
				{
					  	LOG.error(MessagesEnum.GM4C_TEF0007E.getCodAndDescription());	
						throw new Exception("[-3] timeout!");
				}
				Thread.sleep(20);
			}
			Optional<TefDto> list = repTef.findById(transactionId);
			if (!list.isPresent())
			{
				span.log("Não encontrado os dados da simulação na base redis");
				LOG.error(MessagesEnum.GM4C_TEF0008E.getCodAndDescription());
				throw new Exception("[-4] Falha ao atualizar a simulacao");
			}
			simulacao = list.get();
			
			if (!simulacao.getRc_debito().startsWith("[0]"))
			{
				span.log("Falha ao fazer o debito "+ simulacao.getRc_debito());
			    LOG.error(MessagesEnum.GM4C_TEF0009E.getCodAndDescription());
				throw new Exception("[-5] falha ao fazer o debito. " + simulacao.getRc_debito());
			}
			if (!simulacao.getRc_credito().startsWith("[0]"))
			{
				span.log("Falha ao fazer o credito. " + simulacao.getRc_credito());
				LOG.error(MessagesEnum.GM4C_TEF0010E.getCodAndDescription());
				throw new Exception("[-6] falha ao fazer o credito. " + simulacao.getRc_credito());
			}
			if (!simulacao.getRc_limite().startsWith("[0]"))
			{
				span.log("Falha ao atualizar o limite. " + simulacao.getRc_limite());
				LOG.error(MessagesEnum.GM4C_TEF0011E.getCodAndDescription());
				throw new Exception("[-7] falha ao atualizar o limite. "  + simulacao.getRc_limite());
			}
			simulacao.setRc_efetivacao("[0] Efetivação concluida");
			span.log("Efetivacao concluida com sucesso!");
		    LOG.info("",MessagesEnum.GM4C_TEF0024I.getCodAndDescription(), simulacao.getRc_efetivacao());
		}
		catch (Exception e)
		{
			simulacao.setRc_efetivacao(e.getMessage());
			simulacao.setMsg_efetivacao("Timeout, transacao demorou para receber retorno do limmite e conta");
			span.setTag("error", true);
			span.log("Erro na efetivação: " + e.getMessage());
			LOG.error(MessagesEnum.GM4C_TEF0012E.getCodAndDescription(), e.getMessage());
			error=true;
		}
		
		span.log("Removendo os dados da efetivacao do Rediz");
		repTef.delete(simulacao);
		span.log("dados da efetivacao removido do Rediz");
		LOG.info(MessagesEnum.GM4C_TEF0005I.getCodAndDescription());	
		ResultadoSimulacaoTefDto resultado = new ResultadoSimulacaoTefDto();
		resultado.setAgencia_destino(simulacao.getAgencia_destino());
		resultado.setConta_destino(simulacao.getConta_destino());
		resultado.setDv_destino(simulacao.getDv_destino());
		resultado.setAgencia_origem(simulacao.getAgencia_origem());
		resultado.setConta_origem(simulacao.getConta_origem());
		resultado.setDv_origem(simulacao.getDv_origem());
		resultado.setTipo_transacao(simulacao.getTipo());
		resultado.setValor(simulacao.getValor());
		resultado.setId_transacao(transactionId);
		resultado.setResultado(simulacao.getRc_efetivacao());
		
		TracerLabels labels = new TracerLabels(simulacao.getTransactionid(), simulacao.getValor(),simulacao.getTipo(),simulacao.getAgencia_origem(), simulacao.getAgencia_destino(), simulacao.getConta_origem(), simulacao.getConta_destino(),simulacao.getRc_efetivacao());
		//this.preencheSpan(span, labels);
		span.finish();
		LOG.info(resultado, MessagesEnum.GM4C_TEF0019I.getCodAndDescription());
		LOG.debug("Fim da efetivação");
		if (error)
		{
			return ResponseEntity.status(500).build();
		}
		return ResponseEntity.ok(resultado);

	}
	
	
	@PostMapping("/simulacao")
	@Timed( value = "app.request.duration", extraTags = { "evento", "simulacao" } )
	public ResponseEntity<ResultadoSimulacaoTefDto> simulaTransferencia(@RequestBody RequestSimulacaoTefDto simulacao, HttpServletRequest request, @RequestHeader HttpHeaders headers)
	{
	
		String transactionId = UUID.randomUUID().toString();
		String correlationId = UUID.randomUUID().toString();
		boolean syntheticTransaction=false;
		
		Map<String, Object> returnValue = new HashMap<>();

		Enumeration<String> hearderNames = request.getHeaderNames();
		    while(hearderNames.hasMoreElements())
		    {
		     String headerName = hearderNames.nextElement();
		     returnValue.put(headerName, request.getHeader(headerName));
          
		 }
	            
		Iterator<String> keys = returnValue.keySet().iterator();
		while (keys.hasNext())
		{
			String key = keys.next();
			Object value = returnValue.get(key);
	        if (key.equalsIgnoreCase("transactionId"))
	        {
	        	transactionId = (String) value;
	        }
	        else if (key.equalsIgnoreCase("correlationId"))
	        {
		        	correlationId = (String) value;
	        }
	        else if (key.equalsIgnoreCase("syntheticTransaction"))
	        {
	        	if (value.toString().contentEquals("true"))
	        	{
	        		syntheticTransaction = true;
	        	}
	        }

		}
	    
	    if (syntheticTransaction)
		{
		    ResultadoSimulacaoTefDto resultado = new ResultadoSimulacaoTefDto();
	        return ResponseEntity.ok(resultado);
	    }

	    
        LOG.setContext(MdcType.TRANSACTION_ID, transactionId); //Transaction
		LOG.setContext(MdcType.CORRELATION_ID, correlationId); //Correlation
		
		LOG.debug("Início da simulação - Method: simulaTransferencia - Simulação: {} - Request: {} - Headers: {}",simulacao,request,headers);
        LOG.info(simulacao, MessageText.REQUEST_RECEIVED_TRANS, "Simulação");
       
		if (syntheticTransaction == true) {
			LOG.warn(MessageText.SYNTHETIC_TRANSACTION);
		}

		Span span =  this.startServerSpan("TEF-simulacao", request);  
		span.setTag("agencia_origem", simulacao.getAgencia_origem());
		span.setTag("agencia_destino", simulacao.getAgencia_destino());
		span.setTag("transaction_id", transactionId);
		span.setTag("correlationId", correlationId);
		
		//Span span =  this.startServerSpan("TEF-simulacao", request);  
		
		//Define o topico onde o evento de simulacao será gravado
		String topico = "tef";
		LOG.debug("Topico {} definido",topico);
		
		//cria o objeto do Avro com a mensagem do evento de simulacao
		Transferencia simulaAvro = Transferencia.newBuilder()
				.setEvento("simulacao")
				.setAgenciaOrigem(simulacao.getAgencia_origem())
				.setContaOrigem(simulacao.getConta_origem())
				.setDvOrigem(simulacao.getDv_origem())
				.setAgenciaDestino(simulacao.getAgencia_destino())
				.setContaDestino(simulacao.getConta_destino())
				.setDvDestino(simulacao.getDv_destino())
				.setValor(simulacao.getValor())
				.setTipoTransacao(simulacao.getTipo_transacao())
				.setSenha(simulacao.getSenha())
				.setIdTransacao(transactionId)
				.build();
		span.log("criado a mensagem Avro para enviar ao kafka: " + simulaAvro );
		LOG.info(simulaAvro, MessagesEnum.GM4C_TEF0007I.getCodAndDescription());
		
		
		//armazena os dados da simulacao na base do cassandra para posterior efetivacao
		TefDto sim = new TefDto();
		sim.setAgencia_destino(simulacao.getAgencia_destino());
		sim.setAgencia_origem(simulacao.getAgencia_origem());
		sim.setConta_destino(simulacao.getConta_destino());
		sim.setConta_origem(simulacao.getConta_origem());
		sim.setDv_destino(simulacao.getDv_destino());
		sim.setDv_origem(simulacao.getDv_origem());
		sim.setEvento("simulacao");
		sim.setId(transactionId);
		sim.setMsg_credito("pendente");
		sim.setMsg_debito("pendente");
		sim.setMsg_efetivacao("pendente");
		sim.setMsg_limite("pendente");
		sim.setMsg_senha("pendente");
		sim.setMsg_simulacao("enviada");
		sim.setRc_credito("[2] pendente");
		sim.setRc_debito("[2] pendente");
		sim.setRc_efetivacao("[2] pendente");
		sim.setRc_limite("[2] pendente");
		sim.setRc_senha("[2] pendente");
		sim.setRc_simulacao("[10] enviada");
		sim.setSenha(simulacao.getSenha());
		sim.setTipo(simulacao.getTipo_transacao());
		sim.setTransactionid(transactionId);
		sim.setValor(simulacao.getValor());
		sim.setTimestamp(new Timestamp(System.currentTimeMillis()).toLocaleString());
		
		repTef.save(sim);
		span.log("Inserido o registro do inicio da simulação no banco de dados do cassandra. Transação: " + transactionId);
		LOG.info(sim, MessagesEnum.GM4C_TEF0008I.getCodAndDescription()); 
		
		//envia a mensagem ao kafka
		kafka.sendMessage(simulaAvro, span, topico, tracer, kafkaSimulacao, "EnviaEventoSimulaTef", correlationId, transactionId, syntheticTransaction);
		Metrics.counter("app.message.publish", "app", "tef", "fluxo", "simulacao", "topico", "tef").increment();
		LOG.info(simulaAvro, MessageText.EVENT_PRODUCED);
	
		
		Date inicio = new Date();
		Date agora = new Date();
		try
		{
			while (!verificaEtapa(transactionId, "simulacao")) 
			{
				agora = new Date();
				if (agora.getTime()-inicio.getTime()>2000)
				{
					Optional<TefDto> list = repTef.findById(transactionId);
					
					if (!list.isPresent())
					{
					LOG.error(MessagesEnum.GM4C_TEF0003E.getCodAndDescription());
					throw new Exception("[-4] Falha ao atualizar a simulacao");
	
					}
					sim = list.get();
					LOG.error(MessagesEnum.GM4C_TEF0004E.getCodAndDescription());
					throw new Exception("[-3] timeout!");
				}

				Thread.sleep(20);
			}
			Optional<TefDto> list = repTef.findById(transactionId);
			
			if (!list.isPresent())
			{
				LOG.error(MessagesEnum.GM4C_TEF0003E.getCodAndDescription());
				throw new Exception("[-4] Falha ao atualizar a simulacao");
			}
			sim = list.get();
			if (!sim.getRc_senha().startsWith("[0]"))
			{
				LOG.info(MessagesEnum.GM4C_TEF0002I.getCodAndDescription());
				throw new Exception("[-8] senha invalida. "  + sim.getRc_senha());
			}
			if (!sim.getRc_debito().startsWith("[0]"))
			{
				LOG.error(MessagesEnum.GM4C_TEF0005E.getCodAndDescription());
				throw new Exception("[-5] falha na conta origem. " + sim.getRc_debito());
			}
			if (!sim.getRc_credito().startsWith("[0]"))
			{
				LOG.error(MessagesEnum.GM4C_TEF0006E.getCodAndDescription());
				throw new Exception("[-6] falha na conta destino. " + sim.getRc_credito());
			}
			if (!sim.getRc_limite().startsWith("[0]"))
			{
				LOG.info(MessagesEnum.GM4C_TEF0012I.getCodAndDescription());
				throw new Exception("[-7] limite recusado. "  + sim.getRc_limite());
			}
			sim.setRc_simulacao("[0] Simulacao concluida");
			LOG.info("", MessagesEnum.GM4C_TEF0023I.getCodAndDescription(),sim.getRc_simulacao());

		}
		catch (Exception e)
		{
			sim.setRc_simulacao(e.getMessage());
			sim.setMsg_simulacao("Erro durante simulacao: " + e.getMessage()); 
			span.log("Erro: " + sim.getRc_simulacao());
			LOG.error(MessagesEnum.GM4C_TEF0002E.getCodAndDescription(),e.getMessage());
			span.setTag("error", true);
			//e.printStackTrace();
		}
		
		repTef.save(sim);
		span.log("Salvando o resultado da simulacao no cassandra");
		LOG.info(MessagesEnum.GM4C_TEF0008I.getCodAndDescription());		
		
		//Prepara resultado
		ResultadoSimulacaoTefDto resultado = new ResultadoSimulacaoTefDto();
		resultado.setAgencia_destino(simulacao.getAgencia_destino());
		resultado.setConta_destino(simulacao.getConta_destino());
		resultado.setDv_destino(simulacao.getDv_destino());
		resultado.setAgencia_origem(simulacao.getAgencia_origem());
		resultado.setConta_origem(simulacao.getConta_origem());
		resultado.setDv_origem(simulacao.getDv_origem());
		resultado.setTipo_transacao(simulacao.getTipo_transacao());
		resultado.setValor(simulacao.getValor());
		resultado.setId_transacao(transactionId);
		resultado.setResultado(sim.getRc_simulacao());
		
		
		TracerLabels labels = new TracerLabels(transactionId, simulacao.getValor(),simulacao.getTipo_transacao(),simulacao.getAgencia_origem(), simulacao.getAgencia_destino(), simulacao.getConta_origem(), simulacao.getConta_destino(),sim.getRc_simulacao());
		//this.preencheSpan(span, labels);
		span.finish();
		
		LOG.info(resultado, MessagesEnum.GM4C_TEF0013I.getCodAndDescription());
		LOG.debug("Fim da simulação");
		LOG.clearContext();
		if (sim.getRc_simulacao().startsWith("[-8]")) //senha invalida
		{
			return new ResponseEntity<>(resultado, HttpStatus.UNAUTHORIZED);
		}
		else if (sim.getRc_simulacao().contains("[-12]") ) //saldo insuficiente
		{
			return new ResponseEntity<>(resultado, HttpStatus.FORBIDDEN);
		}
		else if (sim.getRc_simulacao().contains("[-7]") ) //limite insuficiente
		{
			return new ResponseEntity<>(resultado, HttpStatus.LOCKED);
		}
		else if (sim.getRc_simulacao().startsWith("[-5]") || sim.getRc_simulacao().startsWith("[-6]")) //conta origem ou destino nao encontrada
		{
			return new ResponseEntity<>(resultado, HttpStatus.NOT_FOUND);
		}
		else if (sim.getRc_simulacao().startsWith("[-3]"))
		{
			return new ResponseEntity<>(resultado, HttpStatus.REQUEST_TIMEOUT);
		}
		return ResponseEntity.ok(resultado);
	

		
	}
	
	/**
	 * metodo resposnavel por verificar se uma etapa (efetivacao ou simulacao) já foi concluida
	 * @param idSimulacao id da simulacao
	 * @param evento tipo de evento (simulacao ou efetivacao)
	 * @return true quando a etapa estiver concluida e false quando nao
	 */
	private boolean verificaEtapa(String idSimulacao, String evento)
	{
		LOG.debug("Inicio verificaEtapa - idSimulacao: {} - evento: {}", idSimulacao,evento);
		

		Optional<TefDto> list = repTef.findById(idSimulacao);
		
		
		if (!list.isPresent())
		{
			LOG.info(MessagesEnum.GM4C_TEF0018I.getCodAndDescription());
			return false;
		}
		
		TefDto ev = list.get();
		
		if (evento.equalsIgnoreCase("simulacao"))
		{
			if (ev.getRc_credito().startsWith("[2]") || ev.getRc_debito().startsWith("[2]") || ev.getRc_limite().startsWith("[2]") || ev.getRc_senha().startsWith("[2]"))
			{
				//TODO - Avaliar log GM4C_TEF0016I pois entra no Loop 
				LOG.debug(MessagesEnum.GM4C_TEF0016I.getCodAndDescription());
				return false;
			}
		} 
		else // efetivaao
		{
			if (ev.getRc_credito().startsWith("[2]") || ev.getRc_debito().startsWith("[2]") || ev.getRc_limite().startsWith("[2]"))
			{
				LOG.debug(MessagesEnum.GM4C_TEF0017I.getCodAndDescription());
				return false;
			}
			
		}
		LOG.info(MessagesEnum.GM4C_TEF0015I.getCodAndDescription());
		return true;
	}
	
	private synchronized void atualizaRedis(String id, Object campo, Span span)
	{
		Optional<TefDto> list = repTef.findById(id);
		if (list.isPresent())
		{
			TefDto simulacao = list.get();
			if (campo instanceof Limite)
			{
				Limite limite = (Limite)campo;
				if (limite.getAprovado())
				{
					simulacao.setMsg_limite("Limite aprovado");
					simulacao.setRc_limite("[0] limite aprovado");
				}
				else
				{
					simulacao.setMsg_limite("Limite insuficiente");
					simulacao.setRc_limite("[-1] limite insuficiente");
				}
								
				repTef.save(simulacao);
				span.log(simulacao.getRc_limite());
				LOG.info(simulacao.getRc_limite(), MessagesEnum.GM4C_TEF0021I.getCodAndDescription());
			}
			else if (campo instanceof Senha)
			{
				Senha senha = (Senha)campo;
				if (senha.getAprovado())
				{
					simulacao.setMsg_senha("Senha correta");
					simulacao.setRc_senha("[0] Senha Correta");
					span.log("Senha validada");
				}
				else
				{
					simulacao.setMsg_senha("Senha inválida");
					simulacao.setRc_senha("[-1] Senha invalida");
					span.setTag("error", true);
					span.setTag("error.message", simulacao.getRc_senha());
				}
				repTef.save(simulacao);
				LOG.info(simulacao.getMsg_senha(), MessagesEnum.GM4C_TEF0022I.getCodAndDescription());

			}
			else if (campo instanceof ContaCorrente)
			{
				ContaCorrente conta = (ContaCorrente)campo;
				simulacao.setMsg_debito(conta.getMotivoContaOrigem());
				simulacao.setRc_debito(conta.getMotivoContaOrigem());
				simulacao.setMsg_credito(conta.getMotivoContaDestino());
				simulacao.setRc_credito(conta.getMotivoContaDestino());
				span.setTag("contaOrigemRetorno", conta.getMotivoContaOrigem());
				span.setTag("contaDestinoRetorno", conta.getMotivoContaDestino());
				repTef.save(simulacao);
				LOG.info(simulacao, MessagesEnum.GM4C_TEF0004I.getCodAndDescription());
			}

		}
	}
	
	@KafkaListener(topics="limite", groupId = "tef")
	public void validaLimite(ConsumerRecord<String, Limite> record,  @Headers MessageHeaders headers)
	{
		String transactionId = "";
		String correlationId = "";
		boolean syntheticTransaction=false;
		
		Iterator<String> keys = headers.keySet().iterator();
		while (keys.hasNext())
		{
			String key = keys.next();
			Object value = headers.get(key);
	        if (key.equalsIgnoreCase("transactionId"))
	        {
	        	transactionId = (String) value;
	        }
	        else if (key.equalsIgnoreCase("correlationId"))
	        {
		        	correlationId = (String) value;
	        }
	        else if (key.equalsIgnoreCase("syntheticTransaction"))
	        {
	        	if (value.toString().contentEquals("true"))
	        	{
	        		syntheticTransaction = true;
	        	}
	        }

		}
		if (syntheticTransaction)
		{
			return;
		}
		LOG.setContext(MdcType.TRANSACTION_ID, transactionId); //Transaction
		LOG.setContext(MdcType.CORRELATION_ID, correlationId); //Correlation
		LOG.debug("Inicio validaLimite - record: {} - headers {}", record, headers);
		Span span = this.startConsumerSpan("RespostaLimite", headers, tracer);
		Object t1 = record.value();
		Limite limite = new Gson().fromJson(t1.toString(), Limite.class);
		LOG.info(record.value(), MessageText.EVENT_RECEIVED_TRANS, "limite");
		
		
		
		Metrics.counter("app.message.subscribe", "app", "tef", "fluxo", limite.getEvento(), "topico", "limite").increment();
		LOG.debug(MessagesEnum.GM4C_TEF0007D.getCodAndDescription(), "app.message.subscribe");
		atualizaRedis(transactionId, limite, span);
		
		span.finish();
		
		LOG.debug("Fim validaLimite");
		LOG.clearContext();
	}

	
	@KafkaListener(topics="conta", groupId = "tef")
	public void validaConta(ConsumerRecord<String, ContaCorrente> record,  @Headers MessageHeaders headers)
	{
		String transactionId = "";
		String correlationId = "";
		boolean syntheticTransaction=false;
		
		Iterator<String> keys = headers.keySet().iterator();
		while (keys.hasNext())
		{
			String key = keys.next();
			Object value = headers.get(key);
	        if (key.equalsIgnoreCase("transactionId"))
	        {
	        	transactionId = (String) value;
	        }
	        else if (key.equalsIgnoreCase("correlationId"))
	        {
		        	correlationId = (String) value;
	        }
	        else if (key.equalsIgnoreCase("syntheticTransaction"))
	        {
	        	if (value.toString().contentEquals("true"))
	        	{
	        		syntheticTransaction = true;
	        	}
	        }

		}

		if (syntheticTransaction)
		{
			return;
		}
		LOG.setContext(MdcType.TRANSACTION_ID, transactionId); //Transaction
		LOG.setContext(MdcType.CORRELATION_ID, correlationId); //Correlation
		LOG.debug("Inicio validaConta - record: {} - headers {}", record,headers);
		
		Span span = this.startConsumerSpan("RespostaConta", headers, tracer);
		Object t1 = record.value();
		LOG.info(record.value(), MessageText.EVENT_RECEIVED_TRANS, "conta");
		
		ContaCorrente conta = new Gson().fromJson(t1.toString(), ContaCorrente.class);
		LOG.debug( MessagesEnum.GM4C_TEF0002D.getCodAndDescription(), conta);
		
		atualizaRedis(transactionId, conta, span);
		
		Metrics.counter("app.message.subscribe", "app", "tef", "fluxo", conta.getEvento(), "topico", "conta").increment();
		LOG.debug(MessagesEnum.GM4C_TEF0007D.getCodAndDescription(), "app.message.subscribe");
		
		span.log("Resposta do microserviço de contas salvo no Redis" );
		span.finish();
		
		LOG.debug("Fim validaConta");
		LOG.clearContext();
	}

	
	@KafkaListener(topics="senha", groupId = "tef")
	public void validaSenha(ConsumerRecord<String, Senha> record,  @Headers MessageHeaders headers)
	{
		String transactionId = "";
		String correlationId = "";
		boolean syntheticTransaction=false;
		
		Iterator<String> keys = headers.keySet().iterator();
		while (keys.hasNext())
		{
			String key = keys.next();
			Object value = headers.get(key);
	        if (key.equalsIgnoreCase("transactionId"))
	        {
	        	transactionId = (String) value;
	        }
	        else if (key.equalsIgnoreCase("correlationId"))
	        {
		        	correlationId = (String) value;
	        }
	        else if (key.equalsIgnoreCase("syntheticTransaction"))
	        {
	        	if (value.toString().contentEquals("true"))
	        	{
	        		syntheticTransaction = true;
	        	}
	        }

		}
		
		if (syntheticTransaction)
		{
			return;
		}

		LOG.setContext(MdcType.TRANSACTION_ID, transactionId); //Transaction
		LOG.setContext(MdcType.CORRELATION_ID, correlationId); //Correlation
		
		LOG.debug("Inicio validaSenha - record: {} - headers: {}",record,headers);
		Span span = this.startConsumerSpan("RespostaSenha", headers, tracer);
		Object t1 = record.value();
		LOG.info(record.value(), MessageText.EVENT_RECEIVED_TRANS, "senha");
		
		Senha senha = new Gson().fromJson(t1.toString(), Senha.class);
		atualizaRedis(transactionId, senha, span);
		Metrics.counter("app.message.subscribe", "app", "tef", "fluxo", "simulacao", "topico", "senha").increment();
		LOG.debug(MessagesEnum.GM4C_TEF0007D.getCodAndDescription(), "app.message.subscribe");
		
		span.finish();
		
		LOG.debug("Fim validaSenha");
		LOG.clearContext();
	}
		
}
