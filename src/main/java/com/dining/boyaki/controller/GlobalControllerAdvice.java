package com.dining.boyaki.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalControllerAdvice {
	
	@ExceptionHandler(NoHandlerFoundException.class)
    public String noHandlerFoundException(){
        return "Common/404";
    }
	
}
