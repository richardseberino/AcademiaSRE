package com.gm4c.log4j2.factories;

import org.slf4j.LoggerFactory;

import com.gm4c.log4j2.loggers.SplunkLogger;

public class ContextLoggerFactory {

	private ContextLoggerFactory() {
		super();

	}

	public static SplunkLogger getLogger(Class clazz) {
		return new SplunkLogger(LoggerFactory.getLogger(clazz));
	}

}
