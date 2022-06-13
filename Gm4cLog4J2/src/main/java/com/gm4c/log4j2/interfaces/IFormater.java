package com.gm4c.log4j2.interfaces;

import com.gm4c.log4j2.types.MdcType;

public interface IFormater {

	void setContext(MdcType key, String value);

	void setContext(String correlationId);

	void clearContext();
}
