package com.uni.spring.board.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.GsonBuilder;
import com.uni.spring.board.model.dto.Board;
import com.uni.spring.board.model.dto.PageInfo;
import com.uni.spring.board.model.dto.Reply;
import com.uni.spring.board.model.service.BoardService;
import com.uni.spring.common.CommException;
import com.uni.spring.common.Pagination;

@Controller
public class BoardController {
	
	@Autowired
	public BoardService boardService;
	
	@RequestMapping("listBoard.do")
	public String selectList(@RequestParam(value="currentPage", required = false, defaultValue="1") int currentPage, Model model) {
		
		//@RequestParam(value="currentPage") int currentPage -> 값이 넘어오지 않아서 에러가 발생한다.
		
		//@RequestParam(value="currentPage", required = false) int currentPage 
		//required : 해당 파라미터가 필수인지 여부(기본 값이 true : 필수)
		//required = false : 값을 꼭 받아줄 필요가 없다고 선언한다. -> null이 들어올 수 있다.
		//-> null은 기본형 int에 들어갈 수 없기 때문에 형변환을 할 수 없다는 에러가 발생한다.
		
		//@RequestParam(value="currentPage", required = false, defaultValue="1") int currentPage
		//defaultValue : 넘어오는 값이 null인 경우 해당 파라미터 기본 값을 지정한다.
		
		//총 페이지 개수
		int listCount= boardService.selectListCount();
		System.out.println(listCount);
		
		//한 페이지 당 보여질 페이지 갯수
		//int pageLimit = 10;
		
		//한 페이지 당 보여질 게시물 개수
		//int boardLimit = 5;
		
		//static으로 올라가있기 때문에 new할 필요가 없다.
		PageInfo pi = Pagination.getPageInfo(listCount, currentPage, 10, 5);
		
		ArrayList<Board> list = boardService.selectList(pi);
		
		model.addAttribute("list", list);
		model.addAttribute("pi", pi);
		
		return "board/boardListView";
	}
	
	@RequestMapping("enrollFormBoard.do")
	public String enrollForm() {
		return "board/boardEnrollForm";
	}
	
	@RequestMapping("insertBoard.do")
	public String insertBoard(Board b, HttpServletRequest request, @RequestParam(name="uploadFile", required=false) MultipartFile file) {
		
		System.out.println(b);
		System.out.println(file.getOriginalFilename()); //아무 것도 첨부하지 않으면 빈문자열로 넘어온다.
		
		if(!file.getOriginalFilename().equals("")) {
			//saveFile을 만든다. -> 파일명을 바꿔줄(insert, update에 사용할)
			String changeName = saveFile(file, request);
			
			if(changeName != null) {
				b.setOriginName(file.getOriginalFilename());
				b.setChangeName(changeName);
			}
		}
		
		boardService.insertBoard(b); //return으로 받는 것이 없음
		
		return "redirect:listBoard.do";
	}
	
	//전달 받은 파일을 업로드 시키고 수정된 파일명을 리턴시키는 메소드
	private String saveFile(MultipartFile file, HttpServletRequest request) {
		//파일이 저장될 경로
		String resources = request.getSession().getServletContext().getRealPath("resources");
		String savePath = resources + "\\upload_files\\";
		
		String originName = file.getOriginalFilename();
		String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		
		//확장자
		String ext = originName.substring(originName.lastIndexOf(".")); //. 뒤에 있는 글자를 잘라온다.
		
		String changeName = currentTime + ext;
		System.out.println("changeName : " + changeName);
				
		try {
			//해당하는 경로에 파일명이 생긴다.
			file.transferTo(new File(savePath+changeName));
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CommException("file Upload Error");
		}
		
		return changeName;
	}
	
	@RequestMapping("detailBoard.do")
	public ModelAndView selectBoard(int bno, ModelAndView mv) {
		
		Board b = boardService.selectBoard(bno);
		
		mv.addObject("b", b).setViewName("board/boardDetailView"); //화면전환과 값을 넘긴다.
		
		return mv; //값을 가지고 화면으로 가면 되니까 mv를 리턴
	}
	
	@RequestMapping("updateFormBoard.do")
	public ModelAndView updateForm(int bno, ModelAndView mv) {
		
		mv.addObject("b", boardService.selectBoard(bno))
		.setViewName("board/boardUpdateForm");
		
		return mv;
	}
	
	@RequestMapping("updateBoard.do")
	public ModelAndView updateBoard(Board b, ModelAndView mv, HttpServletRequest request, 
									@RequestParam(name="reUploadFile", required=false) MultipartFile file) {
		
		/*
		 * 1. 기존의 첨부파일 X, 새로 첨부된 파일 X 	
		 * 	  --> originName : null, changeName : null
		 * 
		 * 2. 기존의 첨부파일 X, 새로 첨부된 파일 O		
		 * 	  --> 서버에 업로드 후 
		 * 	  --> originName : 새로첨부된파일원본명, changeName : 새로첨부된파일수정명
		 * 
		 * 3. 기존의 첨부파일 O, 새로 첨부된 파일 X		
		 * 	  --> originName : 기존첨부파일원본명, changeName : 기존첨부파일수정명
		 * 
		 * 4. 기존의 첨부파일 O, 새로 첨부된 파일 O  
		 * 	  --> 서버에 업로드 후	
		 * 	  --> originName : 새로첨부된파일원본명, changeName : 새로첨부된파일수정명
		 * 
		 * -> 새로 첨부된 파일이 있으면 일단 set을 해줘야 한다.
		 */
		//기존 파일이 있으면 넘어온다.
		String orgChangeName = b.getChangeName();
		
		//reUploadFil로 넘어온 file의 originalFileName이 빈 문자열이 아닌 경우 -> 새로 등록된 파일이 있는 경우
		if(!file.getOriginalFilename().equals("")) {
			String changeName = saveFile(file, request);
			
			b.setOriginName(file.getOriginalFilename());
			b.setChangeName(changeName);
		}
		
		boardService.updateBoard(b);
		
		//기존의 업로드 된 파일을 지운다. -> 게시글을 삭제하는 경우에도 파일을 삭제해야 하기 때문에 메소드로 따로 빼놓는다.
		if(orgChangeName != null) { //기존 changeName이 있는 경우
			deleteFile(orgChangeName, request);
		}
		
		mv.addObject("bno", b.getBoardNo()).setViewName("redirect:detailBoard.do");
		return mv;
	}

	private void deleteFile(String orgChangeName, HttpServletRequest request) {
		//지우기 위해서 경로 필요
		String resources = request.getSession().getServletContext().getRealPath("resources");
		String savePath = resources + "\\upload_files\\";
		
		File deleteFile = new File(savePath + orgChangeName);
		
		deleteFile.delete();
	}
	
	@RequestMapping("deleteBoard.do")
	public String deleteBoard(int bno, String fileName, HttpServletRequest request) {
		
		boardService.deleteBoard(bno);
		
		//넘어오는 fileName이 빈문자열이 아닐때 -> 기존 첨부 파일이 있을 때
		if(!fileName.equals("")) { //fileName이 있다면 -> 첨부된 파일이 있다면
			deleteFile(fileName, request); //파일 삭제
		}
		
		//삭제 후 list로 간다.
		return "redirect:listBoard.do";
	}
	
	//댓글
	@ResponseBody //응답을 보낼때(view 화면이 아닌 ajax나 다른 타입에 보낼때는 @ResponseBody을 사용한다.) -> 그 페이지가 아니라 데이터 자체를 전달
	@RequestMapping(value="rlistBoard.do", produces="application/json; charset=utf-8") //json을 사용할 때 characterset을 설정해야한다.
	public String selectReplyList(int bno) {
		
		ArrayList<Reply> list = boardService.selectReplyList(bno);
		
		return new GsonBuilder().setDateFormat("yyyy년 MM월 dd일 HH:mm:ss").create().toJson(list);

	}
	
	@ResponseBody 
	@RequestMapping(value="rinsertBoard.do")
	public String insertReply(Reply r) {
		int result = boardService.insertReply(r);
		
		return String.valueOf(result); //String으로 형변환해서 return
	}
	
	@ResponseBody 
	@RequestMapping(value="topListBoard.do", produces="application/json; charset=utf-8") 
	public String selectTopList() {
		
		ArrayList<Board> list = boardService.selectTopList();
		
		return new GsonBuilder().setDateFormat("yyyy년 MM월 dd일 HH:mm:ss").create().toJson(list);

	}
}
