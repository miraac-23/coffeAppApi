package com.example.coffeApp.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;


import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RequestLoggerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestStr = request.getRequestURI();
        String requestMethod = request.getMethod();
        String queryString = request.getQueryString();
        String queryClause = StringUtils.hasLength(queryString) ? "?" + queryString : "";
        String params = request.getParameterMap().entrySet().stream().map(entry -> entry.getKey() + ":" +
                Arrays.toString(entry.getValue())).collect(Collectors.joining(", "));

        String message = "Prehandle " +  request.getMethod() + " " + requestStr + queryClause;

        LoggerDetail loggerDetail = new LoggerDetail();
        loggerDetail.getValues().put("requestURI", requestStr);
        loggerDetail.getValues().put("queryString", queryString);
        loggerDetail.getValues().put("requestMethod", requestMethod);
        loggerDetail.getValues().put("requestParams", params);
        Object servletUri = request.getAttribute("javax.servlet.error.request_uri");
        if (servletUri != null) {
            loggerDetail.getValues().put("javaxErrorRequestURI", servletUri.toString());
        }

        log.info(message, loggerDetail);
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception) {

        String requestStr = request.getRequestURI();
        String requestMethod = request.getMethod();
        String queryString = request.getQueryString();
        String queryClause = StringUtils.hasLength(queryString) ? "?" + queryString : "";
        String params = request.getParameterMap().entrySet().stream().map(entry -> entry.getKey() + ":" +
                Arrays.toString(entry.getValue())).collect(Collectors.joining(", "));

        String message = request.getMethod() + " " + requestStr + queryClause;

        LoggerDetail loggerDetail = new LoggerDetail();
        loggerDetail.getValues().put("requestURI", requestStr);
        loggerDetail.getValues().put("queryString", queryString);
        loggerDetail.getValues().put("requestMethod", requestMethod);
        loggerDetail.getValues().put("requestParams", params);

        if (exception == null && response.getStatus() == HttpStatus.OK.value()) {
            log.info(message + " OK", loggerDetail);
        } else if (exception != null) {
            log.error(message + " FAILED", loggerDetail, exception);
        }
    }
}
