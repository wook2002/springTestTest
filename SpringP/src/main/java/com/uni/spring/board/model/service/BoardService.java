package com.uni.spring.board.model.service;

import java.util.ArrayList;

import com.uni.spring.board.model.dto.Board;
import com.uni.spring.board.model.dto.PageInfo;
import com.uni.spring.board.model.dto.Reply;

public interface BoardService {

	int selectListCount();

	ArrayList<Board> selectList(PageInfo pi);

	void insertBoard(Board b);

	Board selectBoard(int bno);

	void updateBoard(Board b);

	void deleteBoard(int bno);

	ArrayList<Reply> selectReplyList(int bno);

	int insertReply(Reply r);

	ArrayList<Board> selectTopList();

}
