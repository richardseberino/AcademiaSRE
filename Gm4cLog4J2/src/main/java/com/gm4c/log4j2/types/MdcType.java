package com.gm4c.log4j2.types;

public enum MdcType {
	TRANSACTION_ID("TransactionId"), CORRELATION_ID("CorrelationId"), PAYLOAD("Payload");

	MdcType(String MdcText) {
		this.MdcText = MdcText;
	}

	private String MdcText;

	public String getMdcText() {
		return MdcText;
	}
}