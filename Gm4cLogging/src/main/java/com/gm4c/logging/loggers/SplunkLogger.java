package com.gm4c.logging.loggers;

import org.slf4j.Logger;

import com.gm4c.logging.adapters.Slf4JWrapper;

public class SplunkLogger extends Slf4JWrapper{
	
	public SplunkLogger(Logger logger) {
		super(logger);
	}

}
