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
public class MemberServiceImpl2 { //일반 클래스

	//connection 연결
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	//어노테이션에 특별한 명칭을 정하지 않는 경우 camel case로 자동 지정된다. -> 웬만하면 아무것도 지정하지 않는 것이 가독성이 좋다.
	@Autowired
	private MemberDao memberDao;
	
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

}
