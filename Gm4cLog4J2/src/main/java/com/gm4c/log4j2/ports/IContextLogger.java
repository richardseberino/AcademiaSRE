package com.gm4c.log4j2.ports;

import com.gm4c.log4j2.interfaces.IFormater;
import com.gm4c.log4j2.interfaces.ILogger;
import com.gm4c.log4j2.types.MdcType;

public interface IContextLogger extends IFormater, ILogger {

	void setContext(MdcType key, String value);

}
