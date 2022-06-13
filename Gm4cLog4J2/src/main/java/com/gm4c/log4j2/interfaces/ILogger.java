package com.gm4c.log4j2.interfaces;

import com.gm4c.log4j2.types.MessageText;

public interface ILogger {

	String getName(); 

	boolean isTraceEnabled(); 
	void trace(String msg); 
	void trace(String format, Object arg); 
	void trace(String format, Object... arguments); 
	void trace(String msg, Throwable t); 
	
	boolean isDebugEnabled(); 
	void debug(String msg); 
	void debug(String format, Object arg); 
	void debug(String format, Object... arguments); 
	void debug(String msg, Throwable t); 
	
	boolean isInfoEnabled(); 
	void info(String msg); 
	void info(String msg, Throwable t); 
	void info(Object avro, String format, Object... arguments); 
	void info( Object object, MessageText format, Object... arguments); 
	
	boolean isWarnEnabled(); 
	void warn(String msg); 
	void warn(String format, Object arg); 
	void warn(String format, Object... arguments); 
	void warn(MessageText format, Object... arguments); 
	void warn(String msg, Throwable t); 
	
	boolean isErrorEnabled(); 
	void error(String msg); 
	void error(String format, Object arg); 
	void error(String format, Object... arguments); 
	void error(MessageText format, Object... arguments); 
	void error(String msg, Throwable t); 
}
