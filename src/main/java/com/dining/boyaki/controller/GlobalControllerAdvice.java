package com.dining.boyaki.controller;

import java.lang.NumberFormatException;
import java.text.ParseException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalControllerAdvice {
	
	@ExceptionHandler(NoHandlerFoundException.class)
    public String noHandlerFoundException(){
        return "Common/404";
    }
	
	@ExceptionHandler(value= {ParseException.class
			                 ,NumberFormatException.class})
    public String Parse_NumberFormatException(){
        return "Common/404";
    }
	
}
