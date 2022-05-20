package com.uni.spring.member.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.uni.spring.member.model.dto.Member;

@Repository //db에 접근하는 클래스에 부여하는 어노테이션
public class MemberDao {

	public Member loginMember(SqlSessionTemplate sqlSession, Member m) {
		
		Member m1 = sqlSession.selectOne("memberMapper.loginMember", m);
		
		return m1;
	}

	public int insertMember(SqlSessionTemplate sqlSession, Member m) {
		
		return sqlSession.insert("memberMapper.insertMember", m);
	}

	public int updateMember(SqlSessionTemplate sqlSession, Member m) {
		
		return sqlSession.update("memberMapper.updateMember", m);
	}

	public int updatePwd(SqlSessionTemplate sqlSession, Member m) {
		
		return sqlSession.update("memberMapper.updatePwd", m);
	}

	public int idCheck(SqlSessionTemplate sqlSession, String userId) {
		
		return sqlSession.selectOne("memberMapper.idCheck", userId);
	}
	
}
