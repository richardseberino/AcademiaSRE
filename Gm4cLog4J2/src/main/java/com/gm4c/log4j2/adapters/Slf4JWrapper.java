package com.gm4c.log4j2.adapters;

import static com.gm4c.log4j2.types.MdcType.PAYLOAD;

import java.util.Map;
import java.util.Optional;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm4c.log4j2.interfaces.IFormater;
import com.gm4c.log4j2.ports.IContextLogger;
import com.gm4c.log4j2.types.MdcType;
import com.gm4c.log4j2.types.MessageText;

public class Slf4JWrapper implements IContextLogger, IFormater {

	private Logger logger;

	public Slf4JWrapper(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void setContext(String transactionId) {
		ThreadContext.put("CorrelationId",
				Optional.ofNullable(transactionId).orElseThrow(() -> new IllegalArgumentException("")));
	}

	@Override
	public void setContext(MdcType key, String value) {
		ThreadContext.put(key.getMdcText(), Optional.ofNullable(value)
				.orElseThrow(() -> new IllegalArgumentException("Contexto não pode ser nulo")));
	}

	@Override
	public void clearContext() {
		ThreadContext.clearAll();
	}

	@Override
	public String getName() {
		return this.logger.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return this.logger.isTraceEnabled();
	}

	@Override
	public void trace(String msg) {
		this.logger.trace(msg);
	}

	@Override
	public void trace(String format, Object arg) {
		this.logger.trace(format, arg);
	}

	@Override
	public void trace(String format, Object... arguments) {
		this.logger.trace(format, arguments);
	}

	@Override
	public void trace(String msg, Throwable t) {
		this.logger.trace(msg, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return this.logger.isDebugEnabled();
	}

	@Override
	public void debug(String msg) {
		this.logger.debug(msg);
	}

	@Override
	public void debug(String format, Object arg) {
		this.logger.debug(format, arg);
	}

	@Override
	public void debug(String format, Object... arguments) {
		this.logger.debug(format, arguments);
	}

	@Override
	public void debug(String msg, Throwable t) {
		this.logger.debug(msg, t);
	}

	@Override
	public boolean isInfoEnabled() {
		return this.logger.isInfoEnabled();
	}

	@Override
	public void info(String msg) {
		this.logger.info(msg);
	}

	public void info(Object object, String format, Object... arguments) {
		ObjectMapper objectMapper = new ObjectMapper();
		if (this.logger.isInfoEnabled()) {
			try {
				if (object instanceof Map) {
					// serializa objeto utilizando o mapper
					setContext(PAYLOAD, objectMapper.writeValueAsString(object));
					this.logger.info(format, arguments);
				} else if (object instanceof SpecificRecordBase) {
					// serializa objeto utilizando o builder do evento - gerado
					// pelo avro
					setContext(PAYLOAD, objectMapper.writeValueAsString(object));
					this.logger.info(format, arguments);
				} else {
					// serializa demais casos
					setContext(PAYLOAD, objectMapper.writeValueAsString(object));
					this.logger.info(format, arguments);
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("Erro ao formatar log", e);
			}
		}
	}

	@Override
	public void info(Object object, MessageText format, Object... arguments) {
		ObjectMapper objectMapper = new ObjectMapper();
		if (this.logger.isInfoEnabled()) {
			try {
				if (object instanceof Map) {
					// serializa objeto utilizando o mapper
					setContext(PAYLOAD, objectMapper.writeValueAsString(object));
					this.logger.info(format.getMessageText(), arguments);
					;
				} else if (object instanceof SpecificRecordBase) {
					// serializa objeto utilizando o builder do evento - gerado
					// pelo avro
					setContext(PAYLOAD, objectMapper.writeValueAsString(object));
					this.logger.info(format.getMessageText(), arguments);
				} else {
					// serializa demais casos
					setContext(PAYLOAD, objectMapper.writeValueAsString(object));
					this.logger.info(format.getMessageText(), arguments);
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("Erro ao formatar log", e);
			}
		}
	}

	@Override
	public void info(String msg, Throwable t) {
		this.logger.info(msg, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return this.logger.isWarnEnabled();
	}

	@Override
	public void warn(String msg) {
		this.logger.warn(msg);
	}

	@Override
	public void warn(String format, Object arg) {
		this.logger.warn(format, arg);
	}

	@Override
	public void warn(String format, Object... arguments) {
		this.logger.warn(format, arguments);
	}

	@Override
	public void warn(MessageText format, Object... arguments) {
		this.logger.warn(format.getMessageText(), arguments);
	}

	@Override
	public void warn(String msg, Throwable t) {
		this.logger.warn(msg, t);
	}

	@Override
	public boolean isErrorEnabled() {
		return this.logger.isErrorEnabled();
	}

	@Override
	public void error(String msg) {
		this.logger.error(msg);
	}

	@Override
	public void error(String format, Object arg) {
		this.logger.error(format, arg);
	}

	@Override
	public void error(String format, Object... arguments) {
		this.logger.error(format, arguments);
	}

	@Override
	public void error(MessageText format, Object... arguments) {
		this.logger.error(format.getMessageText(), arguments);
	}

	@Override
	public void error(String msg, Throwable t) {
		this.logger.error(msg, t);
	}
}