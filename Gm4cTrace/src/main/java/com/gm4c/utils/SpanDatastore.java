package com.gm4c.utils;

import io.opentracing.Span;

public abstract class SpanDatastore {
       
       private ThreadLocal<Span> currentSpan = new ThreadLocal<>();

       public Span getCurrentSpan() {
               return currentSpan.get();
       }

       public void setCurrentSpan(Span span) {
               currentSpan.set(span);
       }

       public void clearCurrentSpan() {
               currentSpan.remove();
       }
}