package com.dining.boyaki.controller;

import java.io.IOException;
import java.lang.NumberFormatException;
import java.text.ParseException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.http.HttpStatus;
import com.amazonaws.AmazonServiceException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.ImageReadException;

@ControllerAdvice
public class GlobalControllerAdvice {
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoHandlerFoundException.class)
    public String noHandlerFoundException(){
        return "error/404";
    }
	
	@ExceptionHandler(value= {ParseException.class
			                 ,NumberFormatException.class})
    public String Parse_NumberFormatException(){
        return "error/404";
    }
	
	@ExceptionHandler(value= {AmazonServiceException.class,IOException.class
	                         ,ImageWriteException.class,ImageReadException.class})
	public String Parse_ImageIoException(){
		return "error/Other";
	}
	
}
