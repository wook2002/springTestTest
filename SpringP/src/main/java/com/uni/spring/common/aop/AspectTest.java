package com.uni.spring.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

//https://docs.spring.io/spring-framework/docs/5.2.9.RELEASE/spring-framework-reference/core.html#aop-at-aspectj
@Aspect
@Component
public class AspectTest {
	//* 임의의 문자열 1개를 의미
	//.. 임의의 문자열 0개이상을 의미
	//*(..) 모든 메서드의미
	
	@Pointcut("execution(* com.uni.spring..*ServiceImpl.*(..))") //comm.umi.spring의 serviceImpl이 붙은 모든 메소드에 실행시키겠다.
	public void pointCut() {}
	
	//@Before("execution(* com.uni.spring..*ServiceImpl.*(..))")
	@Before("pointCut()") //메소드 실행 전에 공통으로 처리할 작업 지정할 때 advice 사용
	public void before(JoinPoint join) {
		
		Signature sig = join.getSignature(); //이 AOP가 적용된 메소드의 정보를 반환 받는다.
		
		Object[] params = join.getArgs(); //파라미터 값 확인
		
		//언제 적용이 되는지 
		for(Object obj : params) {
			System.out.println("obj : " + obj);
		}
		
		//메소드가 제공되는 클래스의 풀 네임 : sig.getDeclaringTypeName()
		//타겟이 되는 메소드 명 : sig.getName()
		//-> 어떤 클래스의 어떤 메소드가 수행?
		System.out.println("메소드 호출 전 확인 : " + sig.getDeclaringTypeName() + " : " + sig.getName());
		
		//sig.getDeclaringTypeName() : 메소드가 있는 클래스의 풀네임
		//sig.getName() : 타켓 객체의 메소드 명
	}
	
	@After("pointCut()") //예외 발생 여부에 상관없이 비지니스 로직 후 무조건 수행하는 기능을 작성하는 advice
	public void after(JoinPoint join) {
		
		Signature sig = join.getSignature(); //이 AOP가 적용된 메소드의 정보를 반환 받는다.

		System.out.println("메소드 호출 후 확인 : " + sig.getDeclaringTypeName() + " : " + sig.getName());		
		//sig.getDeclaringTypeName() : 메소드가 있는 클래스의 풀네임
		//sig.getName() : 타켓 객체의 메소드 명
	}
	
	@AfterReturning(pointcut="pointCut()", returning="returnObj") //정상적으로 비지니스 메소드가 리턴한 결과 데이터를 다른 용도로 처리할 때
	public void returningPoint(JoinPoint join, Object returnObj) {//리턴되는 타입이 어떤건지 몰라서 Object로 받는다.(결과 값을 받는 변수)
		
		Signature sig = join.getSignature(); //이 AOP가 적용된 메소드의 정보를 반환 받는다.

		System.out.println("메소드 호출 후 확인 : " + sig.getDeclaringTypeName() + " : " + sig.getName() + "returnObj : " + returnObj);
	}
	
	@AfterThrowing(pointcut="pointCut()", throwing="exceptionObj") //예외 발생 시 수행
	public void throwingPoint(JoinPoint join, Exception exceptionObj) { //Exception으로 어떤 에러가 뜨는지 받는다.
		
		Signature sig = join.getSignature(); //이 AOP가 적용된 메소드의 정보를 반환 받는다.

		System.out.println("메소드 호출 후 확인 : " + sig.getDeclaringTypeName() + " : " + sig.getName());
		
		if(exceptionObj instanceof IllegalArgumentException) {
			System.out.println("부적합한 값이 입력되었습니다.");
		}else {
			System.out.println("예외 발생 메세지 : " + exceptionObj.getMessage());
			System.out.println("예외 발생 종류 : " + exceptionObj.getClass().getName());
		}
	}
	
	@Around("pointCut()")
	public Object aroundLog(ProceedingJoinPoint join) throws Throwable {
		
		String methodName = join.getSignature().getName();
		
		//스탑워치
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		
		//기준
		Object obj = join.proceed(); //전과 후를 나누는 기준
		
		//처리 후 스탑워치 스탑
		stopwatch.stop();
		
		System.out.println(methodName + " 소요시간(ms) : " + stopwatch.getTotalTimeMillis() + "초");
		
		return obj;
	}
}
