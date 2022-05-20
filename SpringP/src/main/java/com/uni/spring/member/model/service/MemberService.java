package com.uni.spring.member.model.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.uni.spring.member.model.dto.Member;

public interface MemberService {

	Member loginMember(Member m) throws Exception;

	void insertMember(Member m);

	Member loginMember(BCryptPasswordEncoder bCryptPasswordEncoder, Member m);

	Member updateMember(Member m) throws Exception;

	Member updatePwd(Member m) throws Exception;

	int idCheck(String userId);
	
}
