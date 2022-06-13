package com.gm4c.Log4J2.testcases;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm4c.log4j2.domain.MessagesEnum;
import com.gm4c.log4j2.factories.ContextLoggerFactory;
import com.gm4c.log4j2.ports.IContextLogger;
import com.gm4c.log4j2.types.MdcType;
import com.gm4c.log4j2.types.MessageText;
import com.gm4c.objects.Item;
import com.gm4c.objects.User;

public class log4j2Tests {

	// LOG object
	private static final IContextLogger LOG = ContextLoggerFactory.getLogger(log4j2Tests.class);
	// Objects used in the serialization test
	User user = new User(1, "Itau");
	Item myItem = new Item(1, "Computer", user);
	String correlationId = UUID.randomUUID().toString();
	String transactionId = UUID.randomUUID().toString();
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void validateLogIsBeingWrote() {
		LOG.clearContext();
		System.out.println("\n########## Simple Log ##########");
		LOG.info("Simple Log Message");
	}
	
	@Test
	public void validateIfCorrelationIsBeingWrote01() {
		LOG.clearContext();
		LOG.setContext(correlationId);	
		System.out.println("\n########## Simple Log with CorrelationId = " + correlationId +" ##########");
		LOG.info("Simple Log Message with CorrelationId");
	}
	
	@Test
	public void validateIfCorrelationIsBeingWrote02() {
		LOG.clearContext();
		System.out.println("\n########## Simple Log with CorrelationId = " + correlationId +" ##########");
		LOG.setContext(MdcType.CORRELATION_ID, correlationId);	
		LOG.info("Simple Log Message with CorrelationId");
	}
	
	@Test
	public void validateIfTransactionIsBeingWrote() {
		LOG.clearContext();
		System.out.println("\n########## Simple Log with TransactionId = " + transactionId +" ##########");
		LOG.setContext(MdcType.TRANSACTION_ID, transactionId);	
		LOG.info("Simple Log Message with TransactionId");
	}
	
	@Test
	public void validateIfTransactionAndCorrelationAreBeingWrote() {
		LOG.clearContext();
		System.out.println("\n########## Simple Log with TransactionId = " + transactionId +" and CorrelationId = " + correlationId + " ##########");
		LOG.setContext(MdcType.TRANSACTION_ID, transactionId);	
		LOG.setContext(MdcType.CORRELATION_ID, correlationId);	
		LOG.info("Simple Log Message with TransactionId and CorrelationId");
	}
	
	@Test
	public void validateObjectIsBeingSerialized() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		LOG.clearContext();
		System.out.println("\n########## Simple Log with Object Serialized " + "##########");
		System.out.println("Serialized Object" + objectMapper.writeValueAsString(myItem));
		LOG.info( myItem, "Simple Log with Serialized Object");
	}
	
	@Test
	public void validateBultInLogMessageIsBeingWrote() throws JsonProcessingException {
		LOG.clearContext();
		LOG.setContext(MdcType.TRANSACTION_ID, transactionId);	
		LOG.setContext(MdcType.CORRELATION_ID, correlationId);	
		System.out.println("\n########## Simple Log with Object Serialized and Library Built-in Message " + "##########");
        LOG.info( myItem, MessageText.REQUEST_RECEIVED_TRANS, "Simulação");
	}
	
	@Test
	public void validateAppDomainLogCatalogIsBeingWrote() throws JsonProcessingException {
		LOG.clearContext();
		LOG.setContext(MdcType.TRANSACTION_ID, transactionId);	
		LOG.setContext(MdcType.CORRELATION_ID, correlationId);	
		System.out.println("\n########## Simple Log with Object Serialized and App Catalog Message " + "##########");
        LOG.info(myItem, MessagesEnum.GM4C_TEF0001I.getCodAndDescription());
	}
	
}
