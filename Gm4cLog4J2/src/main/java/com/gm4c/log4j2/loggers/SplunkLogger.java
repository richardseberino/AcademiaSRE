package com.gm4c.log4j2.loggers;

import org.slf4j.Logger;

import com.gm4c.log4j2.adapters.Slf4JWrapper;

public class SplunkLogger extends Slf4JWrapper {

	public SplunkLogger(Logger logger) {
		super(logger);
	}

}
