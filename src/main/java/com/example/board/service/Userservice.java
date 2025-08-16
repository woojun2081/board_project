package com.example.board.service;

import com.example.board.dao.UserDao;
import com.example.board.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//트랜잭션 단위로 시행될 메소드 선언 클래스, 스프링이 관리하는 Bean

@Service
@RequiredArgsConstructor //lombok이 final 필들르 초기화하는 생성자를 자동으로 생성.
public class Userservice {
    private final UserDao userDao;

    //보통 service에서는  @Transactional을 붙여서 하나의 트랜잭션으로 처리.
    @Transactional
    public User addUser(String email,String id, String password, String nickname){
        User user = userDao.addUser(email,id,password, nickname);
        userDao.mappingUserRole(user.getUserId()); //권한 부여.
        return user;

    }

    @Transactional
    public User getUser(String id){
        return userDao.getUser(id);
    }

    @Transactional(readOnly = true)
    public List<String> getRoles(int userId) {
        return userDao.getRoles(userId);
    }
}
