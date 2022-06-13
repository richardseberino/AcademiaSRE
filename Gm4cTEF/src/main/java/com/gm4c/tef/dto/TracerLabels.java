package com.gm4c.tef.dto;

public class TracerLabels {

	private String transactionId;
	
	private float valor;
	
	private String tipoTransacao;
	
	
	private int agenciaOrigem;
	private int agenciaDestino;
	private int contaOrigem;
	private int contaDestino;
	
	private String retorno;
	
	

	public String getRetorno() {
		return retorno;
	}

	public void setRetorno(String retorno) {
		this.retorno = retorno;
	}

	public int getAgenciaOrigem() {
		return agenciaOrigem;
	}

	public void setAgenciaOrigem(int agenciaOrigem) {
		this.agenciaOrigem = agenciaOrigem;
	}

	public int getAgenciaDestino() {
		return agenciaDestino;
	}

	public void setAgenciaDestino(int agenciaDestino) {
		this.agenciaDestino = agenciaDestino;
	}

	public int getContaOrigem() {
		return contaOrigem;
	}

	public void setContaOrigem(int contaOrigem) {
		this.contaOrigem = contaOrigem;
	}

	public int getContaDestino() {
		return contaDestino;
	}

	public void setContaDestino(int contaDestino) {
		this.contaDestino = contaDestino;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public float getValor() {
		return valor;
	}

	public void setValor(float valor) {
		this.valor = valor;
	}

	public String getTipoTransacao() {
		return tipoTransacao;
	}

	public void setTipoTransacao(String tipoTransacao) {
		this.tipoTransacao = tipoTransacao;
	}



	public TracerLabels(String transactionId, float valor, String tipoTransacao, int agenciaOrigem,
			int agenciaDestino, int contaOrigem, int contaDestino, String retorno) {
		super();
		this.transactionId = transactionId;
		this.valor = valor;
		this.tipoTransacao = tipoTransacao;
		this.agenciaOrigem = agenciaOrigem;
		this.agenciaDestino = agenciaDestino;
		this.contaOrigem = contaOrigem;
		this.contaDestino = contaDestino;
		this.retorno = retorno;
	}


	
}
