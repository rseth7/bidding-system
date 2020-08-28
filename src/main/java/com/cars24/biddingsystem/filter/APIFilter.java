package com.cars24.biddingsystem.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class APIFilter extends OncePerRequestFilter {
    public static final List<String> excludeUrlPatterns =
            Collections.unmodifiableList(Arrays.asList("/auction*", "/swagger*",
                    "/swagger-resources/**", "/v2/api-docs", "/webjars/**",
                    "/configuration/ui", "/configuration/security", "/auction/**"));

    AntPathMatcher pathMatcher = new AntPathMatcher();
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("APIFilter calling starts");
        filterChain.doFilter(request, response);
        log.info("APIFilter calling ends");
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
        return excludeUrlPatterns.stream().
                anyMatch((path) -> pathMatcher.match(path, request.getServletPath()));
    }
}