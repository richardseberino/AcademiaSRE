package com.gm4c.tef.dto;

import java.io.Serializable;

import javax.persistence.Id;

import org.springframework.data.redis.core.RedisHash;

@RedisHash("TefDto")
public class TefDto implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getAgencia_origem() {
		return agencia_origem;
	}

	public void setAgencia_origem(int agencia_origem) {
		this.agencia_origem = agencia_origem;
	}

	public int getConta_origem() {
		return conta_origem;
	}

	public void setConta_origem(int conta_origem) {
		this.conta_origem = conta_origem;
	}

	public int getDv_origem() {
		return dv_origem;
	}

	public void setDv_origem(int dv_origem) {
		this.dv_origem = dv_origem;
	}

	public int getAgencia_destino() {
		return agencia_destino;
	}

	public void setAgencia_destino(int agencia_destino) {
		this.agencia_destino = agencia_destino;
	}

	public int getConta_destino() {
		return conta_destino;
	}

	public void setConta_destino(int conta_destino) {
		this.conta_destino = conta_destino;
	}

	public int getDv_destino() {
		return dv_destino;
	}

	public void setDv_destino(int dv_destino) {
		this.dv_destino = dv_destino;
	}

	public String getEvento() {
		return evento;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}

	public String getMsg_credito() {
		return msg_credito;
	}

	public void setMsg_credito(String msg_credito) {
		this.msg_credito = msg_credito;
	}

	public String getMsg_debito() {
		return msg_debito;
	}

	public void setMsg_debito(String msg_debito) {
		this.msg_debito = msg_debito;
	}

	public String getMsg_efetivacao() {
		return msg_efetivacao;
	}

	public void setMsg_efetivacao(String msg_efetivacao) {
		this.msg_efetivacao = msg_efetivacao;
	}

	public String getMsg_limite() {
		return msg_limite;
	}

	public void setMsg_limite(String msg_limite) {
		this.msg_limite = msg_limite;
	}

	public String getMsg_senha() {
		return msg_senha;
	}

	public void setMsg_senha(String msg_senha) {
		this.msg_senha = msg_senha;
	}

	public String getMsg_simulacao() {
		return msg_simulacao;
	}

	public void setMsg_simulacao(String msg_simulacao) {
		this.msg_simulacao = msg_simulacao;
	}

	public String getRc_credito() {
		return rc_credito;
	}

	public void setRc_credito(String rc_credito) {
		this.rc_credito = rc_credito;
	}

	public String getRc_debito() {
		return rc_debito;
	}

	public void setRc_debito(String rc_debito) {
		this.rc_debito = rc_debito;
	}

	public String getRc_efetivacao() {
		return rc_efetivacao;
	}

	public void setRc_efetivacao(String rc_efetivacao) {
		this.rc_efetivacao = rc_efetivacao;
	}

	public String getRc_limite() {
		return rc_limite;
	}

	public void setRc_limite(String rc_limite) {
		this.rc_limite = rc_limite;
	}

	public String getRc_senha() {
		return rc_senha;
	}

	public void setRc_senha(String rc_senha) {
		this.rc_senha = rc_senha;
	}

	public String getRc_simulacao() {
		return rc_simulacao;
	}

	public void setRc_simulacao(String rc_simulacao) {
		this.rc_simulacao = rc_simulacao;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTransactionid() {
		return transactionid;
	}

	public void setTransactionid(String transacionid) {
		this.transactionid = transacionid;
	}

	public float getValor() {
		return valor;
	}

	public void setValor(float valor) {
		this.valor = valor;
	}

		@Id
		private String id;
		
		private int agencia_origem;

		private int conta_origem;

		private int dv_origem;

		private int agencia_destino;

		private int conta_destino;

		private int dv_destino;

		private String evento;
		
		private String msg_credito;
		
		private String msg_debito;
		
		private String msg_efetivacao;
		
		private String msg_limite;
		
		private String msg_senha;
		
		private String msg_simulacao;
		
		private String rc_credito;
		private String rc_debito;
		private String rc_efetivacao;
		private String rc_limite;
		private String rc_senha;
		private String rc_simulacao;
		private String senha;
		
		private String timestamp;
		
		private String tipo;
		
		private String transactionid;

		private float valor;
		

		
		
		
		
}
