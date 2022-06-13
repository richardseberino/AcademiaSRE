package com.gm4c.logging.factories;

import org.slf4j.LoggerFactory;

import com.gm4c.logging.loggers.SplunkLogger;
import com.gm4c.logging.ports.IContextLogger;

public class ContextLoggerFactory {

	private ContextLoggerFactory() {
	super();

}

	public static IContextLogger getLogger(Class clazz) {
		return new SplunkLogger(LoggerFactory.getLogger(clazz));
	}

}
