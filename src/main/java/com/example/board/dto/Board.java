package com.example.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Board {
    //여기는 dto필드명으로 db 칼럼명과 일치시켜야 오류가 나지 않음.
    private  int boardId;
    private String title;
    private String content;
    private String name;
    private String nickname;
    private int userId;
    private String writer;
    private LocalDateTime redate;
    private int viewCnt;
    private String image;
//리뷰 관련 dto
    private String reviewText;
    private String reviewImage;
}
