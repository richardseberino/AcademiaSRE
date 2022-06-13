package com.gm4c.logging.domain;

public enum MessageEnumLib {
	
    REQUEST_RECEIVED("Requisição Recebida"),
    REQUEST_RECEIVED_TRANS("Requisição Recebida {}"),
    REQUEST_ENVIADA("Requisição Enviada");
	

    
    private String messageEnumLib;
	
    MessageEnumLib(String messageEnumLib) { 
    	this.messageEnumLib = messageEnumLib; 
    } 

    public String getMessageEnumLib() { 

        return messageEnumLib; 

    } 

  


}

