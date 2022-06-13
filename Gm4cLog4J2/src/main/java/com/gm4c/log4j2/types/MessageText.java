package com.gm4c.log4j2.types;
public enum MessageText { 

	REQUEST_SEND("Request Enviado"), 
	REQUEST_SEND_TRANS("Request Enviado {}"), 
	REQUEST_RECEIVED("Request Recebido"), 
	REQUEST_RECEIVED_TRANS("Request Recebido {}"), 
	RESPONSE_SEND("Response Enviado"), 
	RESPONSE_SEND_TRANS("Response Enviado {}"), 
	RESPONSE_RECEIVED("Response Recebido"), 
	RESPONSE_RECEIVED_TRANS("Response Recebido {}"), 
	EVENT_ACCOUNT_RECEIVED("Evento Cadastral Consumido"), 
	EVENT_ACCOUNT_DELETED("Evento Exclus�o Titularidade"), 
	EVENT_RECEIVED("Evento Consumido"), 
	EVENT_RECEIVED_TRANS("Evento Consumido {}"), 
	EVENT_PRODUCED("Evento Produzido"), 
	EVENT_PRODUCED_DL("Evento Produzido em DeadLetter"), 
	MESSAGE_RECEIVED("Mensagem Consumida"), 
	MESSAGE_PRODUCED("Mensagem Produzida"), 
	ACCOUNT_NOT_FOUND("Conta N�o Encontrada: IdConta {} Titularidade {}"), 
	LEGACY_ACCOUNT_NOT_FOUND("Conta N�o Encontrada: Agencia {} Conta {} Dac {} Titularidade {}"), 
	SYNTHETIC_TRANSACTION("Transa��o Sint�tica");

	MessageText( String messageText ) { 
		this.messageText = messageText; 
	} 

	private String messageText; 

	public String getMessageText() { 
		return messageText; 
	} 
} 