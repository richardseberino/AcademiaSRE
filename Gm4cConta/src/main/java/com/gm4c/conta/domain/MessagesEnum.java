package com.gm4c.conta.domain;

public enum MessagesEnum {

    GM4C_CTA0001I("GM4C_CTA0001I","Saldo Insuficiente"),
    GM4C_CTA0002I("GM4C_CTA0002I","Débito realizado com sucesso na Conta Origem"),  
    GM4C_CTA0003I("GM4C_CTA0003I","Crébito realizado com sucesso na Conta Destino"),     
    GM4C_CTA0004I("GM4C_CTA0004I","Mensagem AVRO criada"),
    
    GM4C_CTA0001E("GM4C_CTA0001E","Conta Origem não Encontrada"),
    GM4C_CTA0002E("GM4C_CTA0002E","Conta Destino não Encontrada"),	
    GM4C_CTA0003E("GM4C_CTA0003E","Conta Origem não Existe"),	
    GM4C_CTA0004E("GM4C_CTA0004E","Conta Origem não Encontrada"),	
    GM4C_CTA0005E("GM4C_CTA0005E","Conta Destino não Existe"),	
    GM4C_CTA0006E("GM4C_CTA0006E","Conta Destino não Encontrada"),
    
    GM4C_CTA0001D("GM4C_CTA0001D","Registrando e inicializando a métrica {}"),
    GM4C_CTA0002D("GM4C_CTA0002D","Conta Origem encontrada"),
    GM4C_CTA0003D("GM4C_CTA0003D","Conta Destino encontrada"),
    GM4C_CTA0004D("GM4C_CTA0004D","Produzindo evento no topico {}"),
    GM4C_CTA0005D("GM4C_CTA0005D","Incrementando Métrica {}"),
    
    GM4C_CTA000XX("GM4C_CTA000XX","Xxxx Xxxx Xxxx");
 
	
    private String codMessage; 

    private String descricao; 

  

    MessagesEnum(String codMessage, String descricao) { 

        this.codMessage = codMessage; 

        this.descricao = descricao; 

    } 

  

    public String getCodMessage() { 

        return codMessage; 

    } 

  

    public String getCodAndDescription() { 

        return codMessage + " - " + descricao; 

    } 

}

