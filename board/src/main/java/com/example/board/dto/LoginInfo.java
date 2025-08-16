package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
//@AllArgsConstructor
public class LoginInfo {
    private int userId;
    private String id;
    private String nickname;
    private List<String> roles = new ArrayList<>();

    public LoginInfo(int userId, String id, String nickname){
        this.userId =userId;
        this.id =id;
        this.nickname =nickname;
    }

    public void addRole(String roleName){
        roles.add(roleName);
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
