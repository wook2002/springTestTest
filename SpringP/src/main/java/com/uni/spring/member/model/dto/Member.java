package com.uni.spring.member.model.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Member {
	
	private String userId;
	private String userPwd;
	private String userName;
	private String email;
	private String gender;
	//private int age;
	private String age; // 나중에 회원가입 시 나이를 입력하지 않으면 빈문자열로 넘어온다. -> 아무 값이 없을 때는 int형으로 담기지 않는다.
	private String phone;
	private String address;
	private Date enrollDate;
	private Date modifyDate;
	private String status;
}
