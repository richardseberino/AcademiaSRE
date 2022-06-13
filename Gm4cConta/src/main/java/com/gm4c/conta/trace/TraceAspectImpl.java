package com.gm4c.conta.trace;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.gm4c.aspect.TraceAspect;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "management.trace.enabled", havingValue = "true", matchIfMissing = false)
public class TraceAspectImpl extends TraceAspect {

}