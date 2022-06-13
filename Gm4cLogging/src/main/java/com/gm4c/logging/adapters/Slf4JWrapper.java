package com.gm4c.logging.adapters;

import static com.gm4c.logging.types.MdcType.TRANSACTION_ID;
import static net.logstash.logback.marker.Markers.append;
import static net.logstash.logback.marker.Markers.appendRaw;

import java.util.Map;
import java.util.Optional;

import org.apache.avro.specific.SpecificRecordBase;
import org.slf4j.Logger;
import org.slf4j.MDC;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm4c.logging.ports.IContextLogger;
import com.gm4c.logging.types.MdcType;
import com.gm4c.logging.types.MessageText;

public class Slf4JWrapper implements IContextLogger {

    private static final String STR_PAYLOAD = "Payload"; 
    private Logger logger; 
    private final ObjectMapper mapper; 

    public Slf4JWrapper(Logger logger) { 
        this.logger = logger; 
        this.mapper = new ObjectMapper(); 
    } 

    @Override 
    public void setContext(String transactionId) { 
        MDC.put(TRANSACTION_ID.getMdcText(), Optional.ofNullable(transactionId) 
                .orElseThrow(() -> new IllegalArgumentException("MDC Context nao pode ser nulo"))); 
    } 

    @Override 
    public void setContext(MdcType key, String value) { 
        MDC.put(key.getMdcText(), Optional.ofNullable(value) 
                .orElseThrow(() -> new IllegalArgumentException("MDC Context nao pode ser nulo"))); 
    } 

    @Override 
    public void setContext(String key, String value) { 
        MDC.put(key, Optional.ofNullable(value) 
                .orElseThrow(() -> new IllegalArgumentException("MDC Context nao pode ser nulo"))); 
    } 

    @Override 
    public void clearContext() { 
        MDC.clear(); 
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
        if (this.logger.isInfoEnabled()) { 
            try { 
                if (object instanceof Map) { 
                    // serializa objeto utilizando o mapper 
                    this.logger.info(append(STR_PAYLOAD, this.mapper.convertValue(object, Map.class)), format, 
                            arguments); 
                } else if (object instanceof SpecificRecordBase) { 
                    // serializa objeto utilizando o builder do evento - gerado 
                    // pelo avro 
                    this.logger.info(appendRaw(STR_PAYLOAD, object.toString()), format, arguments); 
                } else { 
                    // serializa demais casos 
                    this.logger.info(append(STR_PAYLOAD, object), format, arguments); 
                } 
            } catch (Exception e) { 
                throw new IllegalArgumentException("Erro ao formatar log", e); 
            } 
        } 
    } 

    @Override 
    public void info(Object object, MessageText format, Object... arguments) { 
        if (this.logger.isInfoEnabled()) { 
            try { 
                if (object instanceof Map) { 
                    // serializa objeto utilizando o mapper 
                    this.logger.info(append(STR_PAYLOAD, this.mapper.convertValue(object, Map.class)), 
                            format.getMessageText(), arguments); 
                } else if (object instanceof SpecificRecordBase) { 
                    // serializa objeto utilizando o builder do evento - gerado 
                    // pelo avro 
                    this.logger.info(appendRaw(STR_PAYLOAD, object.toString()), format.getMessageText(), arguments); 
                } else { 
                    // serializa demais casos 
                    this.logger.info(append(STR_PAYLOAD, object), format.getMessageText(), arguments); 
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