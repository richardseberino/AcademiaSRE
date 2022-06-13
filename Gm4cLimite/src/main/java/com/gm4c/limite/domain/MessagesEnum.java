package com.gm4c.limite.domain;

public enum MessagesEnum {
	
    GM4C_LIM0001I("GM4C_LIM0001I","Recuperando os dados de limite da Agencia {} - Conta {} - DV {} origem"),
    GM4C_LIM0002I("GM4C_LIM0002I","Dados do Limite Recuperados do Cassandra"),
    GM4C_LIM0003I("GM4C_LIM0003I","Limite Atualizado com Sucesso"),
    GM4C_LIM0004I("GM4C_LIM0004I","Limite Aprovado"),
    GM4C_LIM0005I("GM4C_LIM0005I","Limite Insuficiente"),
    GM4C_LIM0006I("GM4C_LIM0006I","Preparando Mensagem AVRO"),
    GM4C_LIM0007I("GM4C_LIM0007I","Mensagem AVRO criada"),
    
    GM4C_LIM0001D("GM4C_LIM0001D","Registrando e inicializando a métrica {}"),
    GM4C_LIM0002D("GM4C_TEF0002D","Produzindo evento no topico {}"),
    GM4C_LIM0003D("GM4C_LIM0003D","Payload da Mensagem Enviada pelo Kafka"),
    GM4C_LIM0004D("GM4C_TEF0004D","Incrementando Métrica {}"),
    
    GM4C_LIM0001E("GM4C_LIM0001E","Erro na Mudança de Limite"),
	
    GM4C_LIM000XX("GM4C_LIM000XX","Xxxx Xxxx Xxxx");

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

