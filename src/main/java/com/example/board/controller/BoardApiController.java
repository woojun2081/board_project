package com.example.board.controller;

import com.example.board.dto.Board;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")

//json url http://localhost:8081/api/boards
@RequiredArgsConstructor
public class BoardApiController {

    private final BoardService boardService;

    // 전체 게시물
    @GetMapping
    public List<Board> getAllBoards() {
        return boardService.getBoardsAll();
    }

    // 특정 게시물
    @GetMapping("/{bid}")
    public Board getBoard(@PathVariable int bid) {
        return boardService.getBoardById(bid); // 이름 변경에 맞춰 호출
    }
}


