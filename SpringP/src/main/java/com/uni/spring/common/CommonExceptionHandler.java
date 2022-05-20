package com.uni.spring.common;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

//com.uni.spring 패키지 안에서 발생하는 모든 에러들이 다 들어오도록
@ControllerAdvice("com.uni.spring")
public class CommonExceptionHandler {

	@ExceptionHandler(value=Exception.class) //어떤 예외를 받을 것이냐
	public ModelAndView controllerExceptionHandler (Exception e) {
		
		e.printStackTrace(); //어떤 에러가 발생하는지
		
		return new ModelAndView("common/errorPage").addObject("msg", e.getMessage()); //화면도 넘겨주고 결과 값도 넘겨준다.
	}
}
