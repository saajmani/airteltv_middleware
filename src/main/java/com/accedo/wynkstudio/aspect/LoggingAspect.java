package com.accedo.wynkstudio.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Aspect for logging
 */
@Aspect
public class LoggingAspect {

	final Logger log = LoggerFactory.getLogger(this.getClass());

	@AfterThrowing(pointcut = "execution(* com.accedo.wynkstudio.*.impl.*.*(..))", throwing = "error")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
		StringBuilder logMessage = new StringBuilder();
		logMessage.append("Log after throwing - ");
		logMessage.append(joinPoint.getTarget().getClass().getName());
		logMessage.append('.');
		logMessage.append(joinPoint.getSignature().getName());

		// append args
		logMessage.append('(');
		Object[] args = joinPoint.getArgs();
		for (int i = 0; i < args.length; i++) {
			logMessage.append(args[i]).append(',');
		}
		if (args.length > 0) {
			logMessage.deleteCharAt(logMessage.length() - 1);
		}
		logMessage.append(')');

		logMessage.append("\nException is : " + error.getMessage());
		log.error(logMessage.toString(), error);
	}

}
