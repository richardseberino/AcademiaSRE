package com.gm4c.logging.domain;

public enum MessagesEnum {
	
    GM4C_TEF0001I("GM4C_TEF0001I","Simulação não encontrada"),
    GM4C_TEF0002I("GM4C_TEF0002I","Senha Inválida"),
    GM4C_TEF0003I("GM4C_TEF0003I","Senha Validada"),
    GM4C_TEF0004I("GM4C_TEF0004I","Resposta do microserviço de contas salvo na base cassandra"),
    GM4C_TEF0005I("GM4C_TEF0005I","Salvando efetivação no Cassandra"),
    GM4C_TEF0006I("GM4C_TEF0006I","Efetivação salva no Cassandra"),
    GM4C_TEF0007I("GM4C_TEF0007I","Mensagem AVRO criada"),
    GM4C_TEF0008I("GM4C_TEF0008I","Registro da Simulação salvo no Cassandra"),
    GM4C_TEF0009I("GM4C_TEF0009I","Limite Aprovado"),
    GM4C_TEF0010I("GM4C_TEF0010I","Limite Insuficiente"),
    GM4C_TEF0011I("GM4C_TEF0011I","Requisição de Simulação Recebida"),
    GM4C_TEF0012I("GM4C_TEF0012I","Limite Recusado"),
    GM4C_TEF0013I("GM4C_TEF0013I","Simulação Concluída"),
    GM4C_TEF0014I("GM4C_TEF0014I","Dados da Simulação Recuperados"),
    GM4C_TEF0015I("GM4C_TEF0015I","Etapa de Simulação/Efetivação Concluída"),
    GM4C_TEF0016I("GM4C_TEF0016I","Etapa de Simulação Não Concluída"),
    GM4C_TEF0017I("GM4C_TEF0017I","Etapa de Efetivação Não Concluída"),
    GM4C_TEF0018I("GM4C_TEF0018I","Etapa de Efetivação/Simulação Vazia"),
    GM4C_TEF0019I("GM4C_TEF0019I","Efetivação Concluída"),
    GM4C_TEF0020I("GM4C_TEF0020I","Requisição de Efetivação Recebida"),
    GM4C_TEF0021I("GM4C_TEF0021I","Limite validado com Sucesso"),
    GM4C_TEF0022I("GM4C_TEF0022I","Validação de Senha"),
    GM4C_TEF0023I("GM4C_TEF0023I","Definindo status da Simulação: {}"),
    GM4C_TEF0024I("GM4C_TEF0024I","Definindo status da Efetivação: {}"),
    
    GM4C_TEF0001E("GM4C_TEF0001E","Simulação já efetivada ou com erro"),
    GM4C_TEF0002E("GM4C_TEF0002E","Erro durante a simulação {}"),
    GM4C_TEF0003E("GM4C_TEF0003E","Falha ao atualizar a simulação"),
    GM4C_TEF0004E("GM4C_TEF0004E","Timeout da Simulação"),
    GM4C_TEF0005E("GM4C_TEF0005E","Falha na conta origem"),
    GM4C_TEF0006E("GM4C_TEF0006E","Falha na conta destino"),
    GM4C_TEF0007E("GM4C_TEF0007E","Timeout da Efetivação"),
    GM4C_TEF0008E("GM4C_TEF0008E","Falha ao atualizar Simulação"),
    GM4C_TEF0009E("GM4C_TEF0009E","Falha ao fazer o Débito"),
    GM4C_TEF0010E("GM4C_TEF0010E","Falha ao fazer o Crédito"),
    GM4C_TEF0011E("GM4C_TEF0011E","Falha ao atualizar Limite"),
    GM4C_TEF0012E("GM4C_TEF0012E","Timeout Microserviço Limite e/ou Conta"),
    
    GM4C_TEF0001D("GM4C_TEF0001D","Montagem do DTO - Data Transfer Object"),
    GM4C_TEF0002D("GM4C_TEF0002D","Conta em validação"),
    GM4C_TEF0003D("GM4C_TEF0003D","Payload da Mensagem Enviada pelo Kafka"),
    GM4C_TEF0004D("GM4C_TEF0004D","Registrando e inicializando a métrica {}"),
    GM4C_TEF0005D("GM4C_TEF0005D","Incrementando Métricas de Efetivação"),
    GM4C_TEF0006D("GM4C_TEF0006D","Produzindo evento no topico {}"),
    GM4C_TEF0007D("GM4C_TEF0007D","Incrementando Métrica {}"),
    
    GM4C_TEF000XX("GM4C_TEF000XX","Xxxx Xxxx Xxxx");

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


