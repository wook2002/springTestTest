package com.uni.spring.member.model.service;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uni.spring.common.CommException;
import com.uni.spring.member.model.dao.MemberDao;
import com.uni.spring.member.model.dto.Member;

//@EnableAspectJAutoProxy //aop 관련 -> 횡단관점
//@Transactional(rollbackFor = {Exception.class, RuntimeException.class}) //트랜잭션 처리를 해주는 어노테이션  -> exception class를 rollback을 시키겠다.
												//@Transactional(noRollbackFor = Exception.class) : 특정 Exeption rollback 시키지 않겠다.
												//@Transactional(rollbackFor = {Exception.class, RuntimeException.class}) : 여러 개 지정할 경우 {} 괄호 사용
@Service //구현체에 어노테이션을 붙인다.
public class MemberServiceImpl implements MemberService {

	//connection 연결
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	//어노테이션에 특별한 명칭을 정하지 않는 경우 camel case로 자동 지정된다. -> 웬만하면 아무것도 지정하지 않는 것이 가독성이 좋다.
	@Autowired
	private MemberDao memberDao;
	
	@Override
	public Member loginMember(Member m) throws Exception {
		
		Member loginUser = memberDao.loginMember(sqlSession, m);
		System.out.println("loginUser : " + loginUser);
		if(loginUser == null) {
			throw new Exception("loginUser 확인");
		}
		
		return loginUser;
	}

	@Override
	public void insertMember(Member m) {
		
		int result = memberDao.insertMember(sqlSession, m);
		
		if(result < 0) {
			//throw new RuntimeException(); //스프링에서 rollback이 되도록 구현되어 있다. -> 따로 빨간 줄이 뜨지 않는다.
			throw new CommException("회원가입에 실패 하였습니다."); //따로 구현한 exception -> RuntimeException을 상속받음
		}
		
	}

	@Override
	public Member loginMember(BCryptPasswordEncoder bCryptPasswordEncoder, Member m) {
		
		Member loginUser = memberDao.loginMember(sqlSession, m); //mapper에서 id로만 조회
		
		if(loginUser == null) {
			throw new CommException("회원가입에 실패 하였습니다."); //만든 exception을 던진다.
		}
		
		//matches(평문, 암호화) -> true, false 반환
		if(!bCryptPasswordEncoder.matches(m.getUserPwd(), loginUser.getUserPwd())) { //mathes 비교해주는 메소드 : 평문, 암호화된 비밀번호 넣기
			throw new CommException("암호 불일치");									 //받아온 m에 담겨있는 비밀번호, loginUser로 db에서 받아온 비밀번호
		}
		
		return loginUser;
	}
	
	//구현체에서 실행시 JdkDynamicAopProxy 실행
	//인터페이스가 아닌 그냥 클래스에 실행 시 CglibAopProxy 실행
	@Override
	public Member updateMember(Member m) throws Exception {
		
		int result = memberDao.updateMember(sqlSession, m);
		//memberDao.insertMember(sqlSession, m);
		
		if(result > 0) {
			Member loginUser = memberDao.loginMember(sqlSession, m);
			return loginUser;
		}else {
			//throw new CommException("회원수정 실패"); //rollback 가능 -> 어노테이션만 붙이면
			throw new Exception("회원수정 실패"); //rollback 안됨 -> 따로 어노테이션에 classFor로 처리		
		}
		
	}

	@Override
	public Member updatePwd(Member m) throws Exception {
		
		int result = memberDao.updatePwd(sqlSession, m);
		
		if(result > 0) {
			Member updateMem = memberDao.loginMember(sqlSession, m);
			return updateMem;
		}else {
			throw new Exception("회원수정 실패");
		}
	}

	@Override
	public int idCheck(String userId) {
		
		int result = memberDao.idCheck(sqlSession, userId);
		
		if(result < 0) {
			throw new CommException("idCheck Error");
		}
		
		return result;
	}

}
