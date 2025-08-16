package com.example.board.service;

import com.example.board.dao.BoardDao;
import com.example.board.dto.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardDao boardDao;

    @Transactional
    public void addBoard(String nickname, String title, String content, Integer userId, String image, String reviewText, String reviewImage) {
        boardDao.addBoard(nickname, title, content, userId, image, reviewText, reviewImage);
    }

    @Transactional(readOnly = true) //select만 할때는 readyOnly를 true로 설정 해줌.
    public int getTotalCount() {
        return boardDao.getTotalCount();
    }

    @Transactional(readOnly = true)
    public List<Board> getBoards(int page) {
        return boardDao.getBoards(page);
    }

    @Transactional
    public Board getBoard(int boardId) {
        return getboard(boardId, true);
    }

    //updateViewCnt가 true면 글 조회수 증가, false면 증가 안함.
    @Transactional
    public Board getboard(int boardId, boolean updateViewCnt) {
        Board board = boardDao.getBoard(boardId);
        if (updateViewCnt) {
            boardDao.updateViewCnt(boardId);
        }
        return board;
    }

    @Transactional
    public void deleteBoard(int userId, int boardId) {
        Board board = boardDao.getBoard(boardId);
        if (board.getUserId() == userId) {
            boardDao.deleteBoard(boardId);
        }
    }

    @Transactional
    public void deleteBoard(int boardId) {
        boardDao.deleteBoard(boardId);
    }

    @Transactional
    public void updateBoard(int boardId, String title, String content) {
        boardDao.updateBoard(boardId, title, content);
    }


    //json을 위한 코드
    // 전체 게시물 가져오기
    public List<Board> getBoardsAll() {
        return boardDao.findAll();
    }

    // 특정 게시물 가져오기
    public Board getBoardById(int bid) {
        return boardDao.findById(bid);
    }
}
