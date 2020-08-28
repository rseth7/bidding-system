package com.cars24.biddingsystem.filter;

import com.cars24.biddingsystem.util.AppUtils;
import com.cars24.biddingsystem.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Enumeration;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        StopWatch stopWatch = new StopWatch();
        updateThreadLocalVariable(httpServletRequest);
        try {
            stopWatch.start();
            doLoggedFilterInternal(httpServletRequest, httpServletResponse, filterChain);
            stopWatch.stop();
        } finally {
            log.info("Total time taken by the request/response is {}", stopWatch.getLastTaskTimeMillis());
            MDC.clear();
        }
    }

    private void updateThreadLocalVariable(HttpServletRequest httpServletRequest) {
        String requestId = httpServletRequest.getHeader(Constants.REQUEST_ID);
        if(StringUtils.isEmpty(requestId)) {
            requestId = AppUtils.generateRequestId();
        }
        MDC.put(Constants.REQUEST_ID, requestId);
        log.info("RequestId is {}", requestId);
    }

    private void doLoggedFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        ConsumingHttpServletRequestWrapper wrappedRequest = wrapRequest(request);
        ContentCachingResponseWrapper wrappedResponse = wrapResponse(response);

        boolean isFirstExecution = !isAsyncDispatch(request);
        boolean isLastExecution = !isAsyncStarted(request);
        if(isFirstExecution) {
            logRequest(wrappedRequest);
        }
        try {
            filterChain.doFilter(request, response);
            writeResponse(wrappedResponse);
        } finally {
            if(isLastExecution) {
                logResponse(wrappedResponse);
            }
        }
    }

    private ConsumingHttpServletRequestWrapper wrapRequest(HttpServletRequest request) {
        ConsumingHttpServletRequestWrapper wrappedRequest;
        if(request instanceof ContentCachingRequestWrapper) {
            wrappedRequest = (ConsumingHttpServletRequestWrapper)request;
        } else {
            wrappedRequest = new ConsumingHttpServletRequestWrapper(
                    new ContentCachingRequestWrapper(request));
        }
        return wrappedRequest;
    }
    private ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        ContentCachingResponseWrapper wrappedResponse;
        if(response instanceof ContentCachingResponseWrapper) {
           wrappedResponse = (ContentCachingResponseWrapper)response;
        } else {
            wrappedResponse = new ContentCachingResponseWrapper(response);
        }
        return wrappedResponse;
    }
    private void writeResponse(ContentCachingResponseWrapper wrappedResponse) throws IOException {
        byte[] body = wrappedResponse.getContentAsByteArray();
        ServletResponse rawResponse = wrappedResponse.getResponse();
        if(body.length > 0) {
            if(!rawResponse.isCommitted()) {
                rawResponse.setContentLength(body.length);
            }
            StreamUtils.copy(body, rawResponse.getOutputStream());
        }
    }

    private void logRequest(ConsumingHttpServletRequestWrapper wrappedRequest) {
        UriComponents uriComponents = getRequestUrl(wrappedRequest);
        HttpHeaders requestHeaders = new HttpHeaders();
        Enumeration<String> headerNames = wrappedRequest.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            requestHeaders.add(headerName, wrappedRequest.getHeader(headerName));
        }
        String request = new String(wrappedRequest.getContentAsByteArray());
        HttpMethod httpMethod = HttpMethod.valueOf(wrappedRequest.getMethod());
        RequestEntity<String> requestEntity = new RequestEntity<>(request,
                requestHeaders, httpMethod, uriComponents.toUri());
        log.info("Incoming : [{}]", requestEntity.toString());
    }

    private void logResponse(ContentCachingResponseWrapper wrappedResponse) throws IOException {
        HttpStatus httpStatus = HttpStatus.valueOf(wrappedResponse.getStatusCode());
        String response = StreamUtils.copyToString(wrappedResponse.getContentInputStream(),
                Charset.defaultCharset());
        HttpHeaders responseHeaders = new HttpHeaders();
        Collection<String> headerNames = wrappedResponse.getHeaderNames();
        headerNames.forEach(key -> responseHeaders.add(key, wrappedResponse.getHeader(key)));
        ResponseEntity<String> responseEntity = new ResponseEntity<>(response, responseHeaders, httpStatus);
        log.info("Outgoing : [{}]", responseEntity.toString());
        wrappedResponse.copyBodyToResponse();
    }

    private UriComponents getRequestUrl(ConsumingHttpServletRequestWrapper wrappedRequest) {
        return UriComponentsBuilder.newInstance().scheme(wrappedRequest.getScheme())
                .host(wrappedRequest.getServerName())
                .port(wrappedRequest.getServerPort())
                .path(wrappedRequest.getRequestURI())
                .query(wrappedRequest.getQueryString()).build();
    }
}
