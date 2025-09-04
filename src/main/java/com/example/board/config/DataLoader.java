package com.example.board.config;

import com.example.board.dao.BoardDao;
import com.example.board.dao.UserDao;
import com.example.board.dto.Board;
import com.example.board.dto.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final BoardDao boardDao;       // Railway DB용
    private final UserDao userDao;         // Railway DB용
    private final BoardDao localBoardDao;  // 로컬 DB용
    private final UserDao localUserDao;    // 로컬 DB용

    // 생성자 주입
    public DataLoader(BoardDao boardDao, UserDao userDao,
                      BoardDao localBoardDao, UserDao localUserDao) {
        this.boardDao = boardDao;
        this.userDao = userDao;
        this.localBoardDao = localBoardDao;
        this.localUserDao = localUserDao;
    }

    @Override
    public void run(String... args) {
        // --- 1. 로컬 User 데이터 읽기 ---
        List<User> localUsers = localUserDao.findAll(); // 로컬 DB에서 모든 사용자 읽기
        for (User u : localUsers) {
            // Railway DB에 존재하지 않으면 삽입
            if (userDao.getUserSafe(u.getId()) == null) {
                User newUser = userDao.addUser(u.getEmail(), u.getId(), u.getPassword(), u.getNickname());
                userDao.mappingUserRole(newUser.getUserId());
            }
        }

        // --- 2. 로컬 Board 데이터 읽기 ---
        List<Board> localBoards = localBoardDao.findAll(); // 로컬 DB에서 모든 게시글 읽기
        for (Board b : localBoards) {
            // Railway DB에 동일 게시글이 없으면 삽입
            if (boardDao.findByIdSafe(b.getBoardId()) == null) {
                boardDao.addBoard(
                        b.getWriter(),
                        b.getTitle(),
                        b.getContent(),
                        b.getUserId(),
                        b.getImage(),
                        b.getReviewText(),
                        b.getReviewImage()
                );
            }
        }
    }
}

