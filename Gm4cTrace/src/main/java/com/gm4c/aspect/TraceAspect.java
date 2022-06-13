package com.gm4c.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.MessageHeaders;

import com.gm4c.annotation.CollectKafkaTrace;
import com.gm4c.annotation.CollectRestTrace;
import com.gm4c.utils.HttpServletRequestExtractAdapter;
import com.gm4c.utils.KafkaHeaderMap;
import com.gm4c.utils.SpanDatastore;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.tag.Tags;

public abstract class TraceAspect {
	private static final String EXECUTION_REST = "@annotation(com.gm4c.annotation.CollectRestTrace)";
	private static final String EXECUTION_KAFKA = "@annotation(com.gm4c.annotation.CollectKafkaTrace)";

	@Autowired
	private SpanDatastore spanDatastore;

	@Autowired
	private Tracer tracer;

	@Around(EXECUTION_REST)
	public Object doFilterRest(ProceedingJoinPoint pjp) throws Throwable {
		Object[] args = pjp.getArgs();
		String operationLabel = (((MethodSignature) pjp.getSignature()).getMethod()
				.getAnnotation(CollectRestTrace.class)).spanName();
		TextMap carrier = getRestMap(args);

		Span span = doStartSpan(carrier, operationLabel);
		spanDatastore.setCurrentSpan(span);
		return pjp.proceed();
	}

	@Around(EXECUTION_KAFKA)
	public Object doFilterKafka(ProceedingJoinPoint pjp) throws Throwable {
		Object[] args = pjp.getArgs();
		String operationLabel = (((MethodSignature) pjp.getSignature()).getMethod()
				.getAnnotation(CollectKafkaTrace.class)).spanName();
		TextMap carrier = getKafkaMap(args);

		Span span = doStartSpan(carrier, operationLabel);
		spanDatastore.setCurrentSpan(span);
		return pjp.proceed();
	}

	private TextMap getRestMap(Object[] args) {
		TextMap result = null;
		for (int i = 0; i < args.length; i++) {
			Object obj = args[i];
			if (obj.getClass().equals(HttpHeaders.class)) {
				HttpHeaders header = (HttpHeaders) obj;
				result = new HttpServletRequestExtractAdapter(header);
				break;
			}
		}
		return result;
	}

	private TextMap getKafkaMap(Object[] args) {
		TextMap result = null;
		for (int i = 0; i < args.length; i++) {
			Object obj = args[i];
			if (obj.getClass().equals(MessageHeaders.class)) {
				MessageHeaders header = (MessageHeaders) obj;
				result = new KafkaHeaderMap(header);
				break;
			}
		}
		return result;
	}

	private Span doStartSpan(TextMap map, String operationLabel) throws Throwable {
		SpanContext parent = tracer.extract(Format.Builtin.HTTP_HEADERS, map);
		Span span = tracer.buildSpan(operationLabel).asChildOf(parent).start();
		Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_SERVER);
		return span;
	}
}
