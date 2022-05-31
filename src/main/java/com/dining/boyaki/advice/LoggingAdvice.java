package com.dining.boyaki.advice;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dining.boyaki.model.entity.AccountUserDetails;

@Aspect
@Component
public class LoggingAdvice {
	
	private final Logger logger;

	public LoggingAdvice() {
	        this.logger = LoggerFactory.getLogger(getClass());
	}
	
	@Before("within(com.dining.boyaki.controller.*)")
	public void controllerInputLog(JoinPoint jp) {
		
		String logMessage = "[" + getSessionId() + "] " + getUserName() + getClassName(jp) 
				                + getSignatureName(jp)  + getArgs(jp);
		logger.info(logMessage);
	}
	
	@AfterReturning(pointcut = "within(com.dining.boyaki.controller.*)",
			        returning = "returnValue")
	public void controllerOutputLog(JoinPoint jp,Object returnValue) {
		String logMessage = "[" + getSessionId() + "] " + getUserName() + getClassName(jp)
				                + getSignatureName(jp)  + getReturnValue(returnValue);
		logger.info(logMessage);
	}
	
	@AfterThrowing(value = "within(com.dining.boyaki.controller.*)",
			       throwing = "e")
	public void outputErrorLog(JoinPoint jp, Throwable e) {
        String logMessage = "[" + getSessionId() + "] " + getUserName() + getClassName(jp)
        		                + getSignatureName(jp)  + getArgs(jp);
        logger.error(logMessage, e);
    }
	
	//メソッドを実行したユーザ名を取得
	private String getUserName() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth.getPrincipal() instanceof AccountUserDetails) {
			AccountUserDetails details = (AccountUserDetails) auth.getPrincipal();
			return details.getUsername() + ':';
		}else {
			return "???:";
		}
	}
	
	//セッションIDの取得
	private String getSessionId() {
		//ServletRequestAttributes：サーブレットリクエストと HTTP セッションスコープからオブジェクトにアクセスできる
		//RequestContextHolder：RequestAttributesオブジェクトの形式でスレッドローカルにWebリクエストを持つ
		//RequestAttributes：リクエストに関連付けられ性オブジェクトにアクセスするためのインタフェース
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession().getId();
    }
	
	//実行されたパッケージ.クラスの取得
	private String getClassName(JoinPoint joinPoint) {
		String packageName = joinPoint.getTarget().getClass().toString();
		String[] className = packageName.replace(" ", "\\.").split("\\.");

        return className[className.length - 1];
    }
	
	//メソッド名の取得
	private String getSignatureName(JoinPoint joinPoint) {
        return "." + joinPoint.getSignature().getName();
    }
	
	//引数の値を取得
	private String getArgs(JoinPoint joinPoint) {
		Object[] arguments = joinPoint.getArgs();
		List<String> argumentStrings = new ArrayList<String>();

		Arrays.stream(arguments).map(s -> Objects.toString(s)) //mapは、集合データ内の各要素を変換するメソッド 
		                        .forEach(s -> argumentStrings.add(s));
		return " args :" + String.join(",", argumentStrings);
	}
	
	//返り値を取得
	private String getReturnValue(Object returnValue) {
        if(returnValue != null) {
        	return " value:" + returnValue.toString();
        }
        return " value:null";
    }

}
