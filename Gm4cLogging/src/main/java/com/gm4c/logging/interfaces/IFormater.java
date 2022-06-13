package com.gm4c.logging.interfaces;

import com.gm4c.logging.types.MdcType;

public interface IFormater {
	void setContext(MdcType key, String value);

	void setContext(String key, String value);

	void setContext(String correlationId);

	void clearContext();
}
