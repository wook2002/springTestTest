package com.uni.spring.member.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.uni.spring.member.model.dto.Member;
import com.uni.spring.member.model.service.MemberService;

//Model에 Attribute 추가할 때 자동으로 설정된 키 값을 세션에 등록 시키는 기능
@SessionAttributes("loginUser") //model의  loginUser라는 키 값으로 객체가 추가되면 자동으로 session에 추가해라 라는 어노테이션
@Controller //빈으로 등록하는 동시 controller 역할을 한다.
public class MemberController {
	
	@Autowired //service는 구현체가 1개 이다. -> service를 구현한 구현체 중에 bean을 찾아서 주입을 해준다.
	private MemberService memberService;
	
	private MemberService memberServiceImpl2;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder; //bean으로 등록했으니 의존성 주입
	
	//controller마다 다 작성해줘야 하는 단점
	/*@ExceptionHandler(value=Exception.class) //어떤 예외를 받을 것이냐
	public ModelAndView controllerExceptionHandler (Exception e) {
		
		e.printStackTrace(); //어떤 에러가 발생하는지
		
		return new ModelAndView("common/errorPageServer").addObject("msg", e.getMessage()); //화면도 넘겨주고 결과 값도 넘겨준다.
	}*/
	
	//1. HttpServletRequest를 통해 전송 받기(기존 jsp/servlet 방식)
/*	@RequestMapping(value="login.do", method=RequestMethod.POST) //@RequestMapping을 붙여줌으로써 HandlerMapping으로 등록된다. -> url 매핑
	public String loginMember(HttpServletRequest request) {
		
		String userId = request.getParameter("userId");
		String userPwd = request.getParameter("userPwd");
		
		System.out.println(userId);
		System.out.println(userPwd);
		
		return "main"; //view name만 던져도 view resolver가 알아서 경로와 확장자를 붙인다. 
					   //리턴되는 문자열을 servlet-context.xml의 view reslover에 의해서 사용자가 보게 될 뷰로 forwarding을 해준다.
	}
*/
	
	//2. @RequestParam 방식 - 스프링에서 제공하는 파라미터를 받아오는 방식
/*	@RequestMapping(value="login.do", method=RequestMethod.POST)
	public String loginMember(@RequestParam("userId") String userId, @RequestParam("userPwd") String userPwd) {
		
		System.out.println(userId);
		System.out.println(userPwd);
		
		return "main";
	}
*/
	
	//3. @RequestParam 생략 - 매개변수 name과 동일하게 작성해야 자동으로 값이 주어진다.
	//속성을 설정할 때는 RequestParam을 생략하면 안된다.
/*	@RequestMapping(value="login.do", method=RequestMethod.POST) //@RequestMapping을 붙여줌으로써 HandlerMapping으로 등록된다. -> url 매핑
	public String loginMember(String userId, String userPwd) {
		
		System.out.println(userId);
		System.out.println(userPwd);

		return "main"; 
	}
*/
	//4. @ModelAttribute 를 이용한 방법 - 요청 파라미터가 많은경우 객체 타입으로 넘겨 받는데 기본 생성자와 setter 를 이용한 주입 방식 이므로 둘중하나라도 
	//없으면 에러 . 반드시 name 속성에 있는 값과 필드명이 동일 해야 하고 setter 작성하는 규칙에 맞게 작성되어야 한다.
/*	@RequestMapping(value="login.do", method=RequestMethod.POST) //@RequestMapping을 붙여줌으로써 HandlerMapping으로 등록된다. -> url 매핑
	public String loginMember(@ModelAttribute Member m) {
		
		System.out.println("Id :" + m.getUserId());
		System.out.println("Pwd :" + m.getUserPwd());

		return "main"; 
	}
*/
	
	//5. @ModelAttribute 생략하고 객체 바로 전달 받는 방식
	//명시되어 있으면 가독성이 좋다.
/*	@RequestMapping(value="login.do", method=RequestMethod.POST) //@RequestMapping을 붙여줌으로써 HandlerMapping으로 등록된다. -> url 매핑
	public String loginMember(Member m, HttpSession session) {
		
		//System.out.println("Id :" + m.getUserId());
		//System.out.println("Pwd :" + m.getUserPwd());
		
		//기존 방법 -> 객체를 만들어서 연결하기
		//MemberService memService = new MemberServiceImpl();
		
		try {
			Member loginUser = memberService.loginMember(m);
			session.setAttribute("loginUser", loginUser); //jsp에서 loginUser를 사용하기 위해서 session에 값을 넣어준다.
		
			//return "main"; -> 포워딩 방식
			return "redirect:/"; //redirect 방식, view reslover는 무시되고, 루트 안에서 jsp 파일을 찾는다.(/(루트) : index.jps) -> 포워딩 되는 것이 아니다.
		
		} catch (Exception e) {
			
			e.printStackTrace();
			return "common/errorPage";
		}		
	}
*/	
	
/*	@RequestMapping("logout.do")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
*/
	
	//응답 페이지에 응답할 데이터가 있는 경우 
	//1. Model 객체 사용하는 방법 - 응답 뷰로 전달하고자 하는 Map(키, 밸류)형식으로 값을 담을 수 있다.
/*	@RequestMapping(value="login.do", method=RequestMethod.POST) //@RequestMapping을 붙여줌으로써 HandlerMapping으로 등록된다. -> url 매핑
	public String loginMember(Member m, HttpSession session, Model model) {		
		
		try {
			Member loginUser = memberService.loginMember(m);
			session.setAttribute("loginUser", loginUser); //jsp에서 loginUser를 사용하기 위해서 session에 값을 넣어준다.
		
			//return "main"; -> 포워딩 방식
			return "redirect:/"; //redirect 방식, view reslover는 무시되고, 루트 안에서 jsp 파일을 찾는다.(/(루트) : index.jps) -> 포워딩 되는 것이 아니다.
		
		} catch (Exception e) {
			
			e.printStackTrace();
			model.addAttribute("msg", "로그인 실패");
			return "common/errorPage";
		}		
	}
*/
	//2. ModelAndView 객체를 사용하는 방법 - 값과 뷰를 모두 지정
/*	@RequestMapping(value="login.do", method=RequestMethod.POST) 
	public ModelAndView loginMember(Member m, HttpSession session, ModelAndView mv) {		
		
		try {
			Member loginUser = memberService.loginMember(m);
			session.setAttribute("loginUser", loginUser); 
			
			//뷰 name만 넘긴다.
			mv.setViewName("redirect:/"); 
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
			//값과 view name을 같이 넘긴다. -> 포워드 하듯이
			mv.addObject("msg", "로그인 실패"); //넘기는 방식은 똑같다. -> 키, 밸류 형식
			mv.setViewName("common/errorPage");
		}	
		
		return mv; //객체를 return
	}
*/
	
	//3. session에 loginUser를 저장할 때 @SessionAttribute 어노테이션 사용하기
/*	@RequestMapping(value="login.do", method=RequestMethod.POST) 
	public String loginMember(Member m, Model model) {		
		System.out.println(m.getUserId());
		try {
			Member loginUser = memberService.loginMember(m);
			model.addAttribute("loginUser", loginUser); //model에 담기만 했지만 session에 자동으로 키값으로 담긴다.
			
			return "redirect:/"; 
			
		} catch (Exception e) {
			
			e.printStackTrace();
			model.addAttribute("msg", "로그인 실패");
			return "common/errorPage";
		}		
	}
*/
	
	//로그아웃 변경 (@SessionAttributes로 설정해놓았기 때문에)
	@RequestMapping("logout.do")
	public String logout(SessionStatus status) { //session의 상태를 관리해주는 SessionStatus
		status.setComplete(); //현재 @SessionAttributes에 저장된 오브젝트를 제거해준다.
		return "redirect:/";
	}
	
	//회원가입 폼으로 화면 전환
	@RequestMapping("enrollForm.do")
	public String enrollForm() {
		return "member/memberEnrollForm";
	}
	
	//비밀번호 암호화 -> pom.xml에서 라이브러리 추가해서 사용
	//솔팅 기법 사용 : apple(일반문자,평문) + 솔트 값 붙임(랜덤 값) 
	//applefjwoeiwjoefji -> dfwfewefdfsfe (솔트 값이 붙은 비밀번호를 암호화 한다.)
	//applebmfieomrvmero -> adfmwlefwieds
	//똑같은 apple이지만 암호화 값이 계속 달라짐
	@RequestMapping("insertMember.do")
	public String insertMember(@ModelAttribute Member m, @RequestParam("post") String post,
			@RequestParam("address1") String address1, @RequestParam("address2") String address2, HttpSession session) { //@RequestParam으로 받는 이유 member 객체에 jsp에서 넘어오는 address1,address2를 넣을 수 있는 필드가 없다. -> 따로 받아서 합치기
		
		System.out.println(m);

		m.setAddress(post + "/" + address1 + "/" + address2);
		
		System.out.println("암호화 전 : " + m.getUserPwd());
		
		String encPwd = bCryptPasswordEncoder.encode(m.getUserPwd());
		
		System.out.println("암호화 후 : " + encPwd);
		
		m.setUserPwd(encPwd); //암호화 된 password 넣어줌
		
		memberService.insertMember(m);
		
		session.setAttribute("msg", "회원가입 성공");
		
		return "redirect:/";
	}
	
	//비밀번호가 암호화 되어 있기 때문에 로그인이 안된다.
	//암호화 처리 후 로그인
	@RequestMapping(value="login.do", method=RequestMethod.POST) 
	public String loginMember(Member m, Model model) {		

		Member loginUser = memberService.loginMember(bCryptPasswordEncoder, m);
		model.addAttribute("loginUser", loginUser); //model에 담기만 했지만 session에 자동으로 키값으로 담긴다.
		
		return "redirect:/"; 
	
	}
	
	//마이페이지로 이동
	@RequestMapping("myPage.do")
	public String myPage() {
		return "member/myPage";
	}
	
	//수정하기
	@RequestMapping("updateMember.do")
	public String updateMember(@ModelAttribute Member m, @RequestParam("post") String post,
			@RequestParam("address1") String address1, @RequestParam("address2") String address2, Model model) throws Exception {
		
		//address setting
		m.setAddress(post + "/" + address1 + "/" + address2);		
		Member userInfo;
		
		userInfo = memberService.updateMember(m);
		//userInfo = memberServiceImpl2.updateMember(m);
		
		model.addAttribute("loginUser", userInfo); //model에 담아줘야 session에도 담긴다 -> @SessionAttributes 어노테이션 사용

		return "member/myPage";
	}
	
	//비밀번호 수정하기
	@RequestMapping("updatePwd.do")
	public String updatePwd(@RequestParam("originPwd") String originPwd, @RequestParam("updatePwd") String updatePwd,
			HttpSession session, Model model, SessionStatus status) {
		
		//session에 담겨있는 loginUser 꺼내오기
		Member m = (Member)session.getAttribute("loginUser");		
		//System.out.println("loginUser : " + loginUser);
		
		//session에 담긴 비밀번호와 회원이 적은 현재 비밀번호 비교
		if(bCryptPasswordEncoder.matches(originPwd, m.getUserPwd())) { //일치할 때
			try {
				String encNewPwd = bCryptPasswordEncoder.encode(updatePwd);
				m.setUserPwd(encNewPwd);
				
				Member updateMem = memberService.updatePwd(m); //암호화된 비밀번호를 넘긴다.
				model.addAttribute("loginUser", updateMem);
				
				session.setAttribute("msg", "비밀번호 변경 성공");
				status.setComplete();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else { //일치하지 않을 때
			session.setAttribute("msg", "현재 비밀번호가 일치하지 않습니다. 다시 입력해주세요.");
			return "member/myPage";
		}
		
		return "redirect:/";
	}
	
	@ResponseBody
	@RequestMapping("idCheck.do")
	public String idCheck(String userId) {
		
		int count = memberService.idCheck(userId);
		
		return String.valueOf(count); //보통 string 기반 요청, 컨트롤러 반환 값으로 int가 있지 않다. int를 한다고 해서 에러가 나지 않지만 파싱할 때 문제가 생길 수 있어서 string으로 바꿔서 보낸다.
	}
}


