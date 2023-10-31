package com.gm4c.senha.domain;

public enum MessagesEnum {
	
	//INFO
    GM4C_SEN0001I("GM4C_SEN0001I","Evento de efetivação, senha nao precisa ser validada, ignorando mensagem"),
    GM4C_SEN0002I("GM4C_SEN0002I","Senha validada com sucesso"),
    GM4C_SEN0003I("GM4C_SEN0003I","Senha inválida"),
    GM4C_SEN0004I("GM4C_SEN0004I","Preparando Mensagem AVRO"),

    //DEBUG
    GM4C_SEN0001D("GM4C_SEN0001D","Registrando e inicializando a métrica {}"),
    GM4C_SEN0002D("GM4C_SEN0002D","Payload da Mensagem Enviada pelo Kafka"),
    
    //ERROR
    GM4C_SEN0001E("GM4C_SEN0001E","Senha nao cadastrada para essa Conta ou conta nao existe"),
    
    GM4C_SEN000XX("GM4C_SEN000XX","Xxxx Xxxx Xxxx"),

    //Warning
    GM4C_SEN0001W("GM4C_SEN00001W", "Senha complexa, criptografia levando mais tempo do que o normal!");

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


