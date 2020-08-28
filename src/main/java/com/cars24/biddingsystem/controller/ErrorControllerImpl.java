package com.cars24.biddingsystem.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ErrorControllerImpl implements ErrorController {
    @Override
    public String getErrorPath() {
        return null;
    }

    @RequestMapping("/error")
    public void handleError(HttpServletRequest request) throws Throwable {
        if(request.getAttribute("javax.servlet.error.exception") != null) {
            throw (Throwable) request.getAttribute("javax.servlet.error.exception");
        }
    }
}
