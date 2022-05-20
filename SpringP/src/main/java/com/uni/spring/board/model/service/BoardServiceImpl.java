package com.uni.spring.board.model.service;

import java.util.ArrayList;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uni.spring.board.model.dao.BoardDao;
import com.uni.spring.board.model.dto.Board;
import com.uni.spring.board.model.dto.PageInfo;
import com.uni.spring.board.model.dto.Reply;
import com.uni.spring.common.CommException;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private SqlSessionTemplate sqlSession;
	
	@Autowired
	private BoardDao boardDao;
	
	@Override
	public int selectListCount() {
		
		return boardDao.selectListCount(sqlSession);
	}

	@Override
	public ArrayList<Board> selectList(PageInfo pi) {
		
		return boardDao.selectList(sqlSession, pi);
	}

	@Override
	public void insertBoard(Board b) {
		
		int result = boardDao.insertBoard(sqlSession, b);
		
		if(result < 0) { //제대로 등록이 안된 경우 -> root-context에 transaction 설정을 해놓음 -> exception 발생되면 roll-back됨
			throw new CommException("게시물 등록에 실패하였습니다.");
		}
	}

	@Override
	public Board selectBoard(int bno) {
		
		Board b = null;
		
		//조회수 올려주기
		int result = boardDao.increaseCount(sqlSession, bno);

		if(result < 0) { //예외가 발생하면 -1이 넘어온다.
			throw new CommException("increaseCount 실패");
		} else {
			b = boardDao.selectBoard(sqlSession, bno);
		}
		
		return b;
	}

	@Override
	public void updateBoard(Board b) {
		
		int result = BoardDao.updateBoard(sqlSession, b);
		
		if(result < 0) {
			throw new CommException("게시물 수정 실패");
		}
		
	}

	@Override
	public void deleteBoard(int bno) {
		
		int result = BoardDao.deleteBoard(sqlSession, bno);
		
		if(result < 0) {
			throw new CommException("게시물 삭제 실패");
		}
		
	}

	@Override
	public ArrayList<Reply> selectReplyList(int bno) {		

		return boardDao.selectReplyList(sqlSession, bno);
	}

	@Override
	public int insertReply(Reply r) {

		int result = boardDao.insertReply(sqlSession, r);
		
		if(result < 0) {
			throw new CommException("댓글 등록 실패");
		}
		
		return result;
	}

	@Override
	public ArrayList<Board> selectTopList() {
		
		return boardDao.selectTopList(sqlSession);
	}

}
