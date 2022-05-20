package com.uni.spring.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.uni.spring.member.model.dto.Member;

import ch.qos.logback.classic.Logger;

public class BListInterceptor extends HandlerInterceptorAdapter{
	//로그를 찍고자 하는 위에다가 작성을 해서 사용한다.
	private static final Logger logger=(Logger) LoggerFactory.getLogger(BListInterceptor.class);
	
	//게시판에 접속하기 전에 로그인이 된 사람만 게시판 이용이 가능하도록
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();
		
		Member loingUser = (Member)session.getAttribute("loginUser");
		
		if(loingUser == null) { //로그인이 안된 경우
			logger.info("비로그인 상태에서 [ " + request.getRequestURI() + " ] 접근 시도!!");
			
			session.setAttribute("msg", "로그인 후 이용하세요");
			response.sendRedirect("/spring");
			
			//컨트롤러가 수행되면 안되기 때문에 false return
			return false;
		}
		//컨트롤러 수행
		return true;
	}
	
	
}
