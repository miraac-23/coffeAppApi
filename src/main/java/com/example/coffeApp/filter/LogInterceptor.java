package com.example.coffeApp.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

@Slf4j
public class LogInterceptor extends TurboFilter {
    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String s,
                              Object[] objects, Throwable throwable) {

        try {
            if (level.levelInt >= logger.getEffectiveLevel().levelInt) {
                removeMDC();
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    MDC.put("user",
                            SecurityContextHolder.getContext().getAuthentication().getName());
                }
                int length = objects == null ? 0 : objects.length;
                if (length > 0 && objects[0] instanceof LoggerDetail) {
                    for (Map.Entry<String, String> entry : ((LoggerDetail) objects[0]).values.entrySet()) {
                        MDC.put(entry.getKey(), entry.getValue());
                    }
                    for (Object obj : objects) {
                        if (throwable == null && obj instanceof Throwable) {
                            throwable = (Throwable) obj;
                            appendExceptionLog(throwable);
                        }
                    }
                } else {
                    if (throwable != null) {
                        appendExceptionLog(throwable);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return FilterReply.NEUTRAL;
    }

    private void appendExceptionLog(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        MDC.put("StackTrace", sw.toString());
        MDC.put("ErrorMessage", throwable.getMessage());
        MDC.put("ExceptionType", throwable.getClass().getSimpleName());
        if (throwable.getCause() != null) {
            MDC.put("Cause", throwable.getCause().getMessage());
        }
    }

    private void removeMDC() {
        MDC.remove("StackTrace");
        MDC.remove("ErrorMessage");
        MDC.remove("ExceptionType");
        MDC.remove("Cause");
        MDC.remove("user");
        MDC.remove("requestBody");
        MDC.remove("requestURI");
        MDC.remove("queryString");
        MDC.remove("requestMethod");
        MDC.remove("requestParams");
        MDC.remove("javaxErrorRequestURI");
    }
}
