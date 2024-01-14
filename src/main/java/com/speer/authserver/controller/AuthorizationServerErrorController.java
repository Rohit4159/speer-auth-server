package com.speer.authserver.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthorizationServerErrorController implements ErrorController {

    Logger logger = LoggerFactory.getLogger(AuthorizationServerErrorController.class);

	@GetMapping("/error")
    public String handleError(HttpServletRequest request) {
		logger.error("Error => {}", request);
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		logger.error("Error status code {}", status);
	    if (status != null) {
	        Integer statusCode = Integer.valueOf(status.toString());	    
	        if(statusCode == HttpStatus.NOT_FOUND.value()) {
	            return "error-404";
	        }
	        else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
	            return "error-500";
	        }
	    }
	    return "error";
    }

}