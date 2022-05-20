package com.uni.spring.common.interceptor;

import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.uni.spring.member.model.dto.Member;

import ch.qos.logback.classic.Logger;

public class LoginInterceptor extends HandlerInterceptorAdapter{
	//로그를 찍고자 하는 위에다가 작성을 해서 사용한다.
	private static final Logger logger=(Logger) LoggerFactory.getLogger(LoginInterceptor.class);

	//로그인 이력을 남기고 싶으면 로그인이 된 뒤에 찍어야 한다 -> post
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		HttpSession session = request.getSession();
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		if(loginUser != null) {
			InetAddress local = InetAddress.getLocalHost();
			logger.info(loginUser.getUserId() + " " + local.getHostAddress());
		}
	}


	
	
	
}
