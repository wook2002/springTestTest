package com.uni.spring.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.uni.spring.member.model.dto.Member;

import ch.qos.logback.classic.Logger;

//로그인 성공 후
@Aspect
@Component
public class AfterReturningAspect {
	private static final Logger logger=(Logger) LoggerFactory.getLogger(AfterReturningAspect.class);
	
	@AfterReturning(pointcut="execution(* com.uni.spring..*ServiceImpl.login*(..))", returning="returnObj") //정상적으로 비지니스 메소드가 리턴한 결과 데이터를 다른 용도로 처리할 때
	public void loggerAdvice(JoinPoint join, Object returnObj) {
		
		if(returnObj instanceof Member) {
			Member m = (Member)returnObj;
			
			if(m.getUserId().equals("admin")) {
				logger.info("[LOG] : 관리자님 환영합니다.");
			}else {
				logger.info("[LOG] : " + m.getUserId() + "로그인 성공!!");
			}
		}
	}
	
}
