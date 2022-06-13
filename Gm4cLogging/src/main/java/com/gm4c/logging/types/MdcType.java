package com.gm4c.logging.types;

public enum MdcType {
	TRANSACTION_ID("TransactionId"),
	CORRELATION_ID("CorrelationId");

	MdcType(String MdcText) {
		this.MdcText = MdcText;
	}

	private String MdcText;

	public String getMdcText() {
		return MdcText;
	}
}