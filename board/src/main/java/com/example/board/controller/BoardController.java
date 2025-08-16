package com.example.board.controller;

import com.example.board.dto.Board;
import com.example.board.dto.LoginInfo;
import com.example.board.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.ssl.SslProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

//HTTP요청 받아서 응답하는 컴포넌트. 스프링 부트가 자동으로 Bean 생성.
@Controller
@RequiredArgsConstructor
public class BoardController {
    private  final BoardService boardService;

    //게시물 목록 보여주기
    // 컨트롤러의 메소드가 리턴하는 문자열은 템플릿 이름.
    //http://localhost:8081/share ------> "list"라는 이름의 템플릿을 사용하여 화면에  출력함.

    //list 리턴은 classpath:/templates/list.html 사용한다는 뜻.--> 프론트 부분
    @GetMapping("/share")
    public String list(@RequestParam(name="page", defaultValue = "1")int page, HttpSession session, Model model){
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        model.addAttribute("loginInfo",loginInfo);

        int totalCount = boardService.getTotalCount();
        List<Board> list = boardService.getBoards(page); //page가 1, 2, 3, 4 ...
        int pageCount = totalCount /10;
        if(totalCount % 10>0){
            pageCount++;
        }
        int currentPage = page;
        model.addAttribute("list",list);
        model.addAttribute("pageCount",pageCount);
        model.addAttribute("currentPage",currentPage);

//        System.out.println("totalCount :" + totalCount);
//        for(Board board : list){
//            System.out.println(board);
//        }

        return "list";
    }

    // /board?id=3 // 파라미터 id, 파라미터 id의 값은 3
    @GetMapping("/board")
    public String board(@RequestParam("boardId")int boardId, Model model){
        System.out.println("boardId: "+boardId);

        //id에 해당하는 게시물 읽어옴.
        // id에 해당하는 게시물의 조회수도 1 증가.

        Board board = boardService.getBoard(boardId);
        model.addAttribute("board",board);
        return "board";
    }
    @GetMapping("/writeform")
    public String writeform(HttpSession session,Model model){
        //로그인한 사용하만 글 쓰기, 세션에서 로그인한 정보 읽어 로그인 하지 않았다면 리스트보기로 자동 이동.

        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo ==null){  //세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginform";
        }

        model.addAttribute("loginInfo",loginInfo);

        return "writeform";
    }
    @PostMapping("/write")
    public String write(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("image")MultipartFile imageFile,
            @RequestParam(value="reviewText", required=false) String reviewText,
            @RequestParam(value="reviewImage", required=false) MultipartFile reviewImageFile,
            HttpSession session
    ){
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo ==null){  //세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginform";
        }
        //로그인한 사용자만 글쓰고, 세션 로그인한 정보 읽어서 로그인 하지 않았다면 리스트보기로 자동 이동.
        System.out.println("title : "+title);
        System.out.println("content : "+content);
        //로그인한 회원 정보 + 제목, 내용 저장.

        String imageName = null;

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String uploadDir = "C:/upload/";
                File uploadDirFile = new File(uploadDir);

                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }

                String originalFilename = imageFile.getOriginalFilename();
                String uniqueFilename = java.util.UUID.randomUUID() + "_" + originalFilename;

                File dest = new File(uploadDirFile, uniqueFilename);
                imageFile.transferTo(dest);

                imageName = uniqueFilename;
            } catch (Exception e) {
                e.printStackTrace();
                imageName = null;
            }
        }

        String reviewImageName = null;
        if (reviewImageFile != null && !reviewImageFile.isEmpty()) {
            try {
                String uploadDir = "C:/upload/";
                File uploadDirFile = new File(uploadDir);

                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }

                String originalFilename = reviewImageFile.getOriginalFilename();
                String uniqueFilename = java.util.UUID.randomUUID() + "_" + originalFilename;

                File dest = new File(uploadDirFile, uniqueFilename);
                reviewImageFile.transferTo(dest);

                reviewImageName = uniqueFilename;
            } catch (Exception e) {
                e.printStackTrace();
                reviewImageName = null;
            }
        }


        boardService.addBoard(loginInfo.getNickname(),title, content, loginInfo.getUserId(), imageName, reviewText, reviewImageName);

        return "redirect:/share";

    }
    @GetMapping("/delete")
    public String delete(
            @RequestParam("boardId") int boardId,
            HttpSession session
    ){
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginform";
        }


        //loginInfo.getUserId인 사람이 글을 쓸 경우에만 삭제함.
        List<String> roles= loginInfo.getRoles();
        if(roles.contains("ROLE_ADMIN")){
            boardService.deleteBoard(boardId);
        }else {
            boardService.deleteBoard(loginInfo.getUserId(), boardId);
        }
        return "redirect:/share";
    }

    @GetMapping("updateform")
    public String updateform(
            @RequestParam("boardId") int boardId, Model model, HttpSession session) {
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null) {
            return "redirect:/loginform";
        }
        //boardId에 해당하는 정보를 읽어와서 updateform 템플릿에게 전달.
        Board board = boardService.getboard(boardId, false);
        model.addAttribute("board", board);
        model.addAttribute("loginfo", loginInfo);
        return "updateform";
    }

    @PostMapping("/update")
    public String update(@RequestParam("boardId") int boardId,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         HttpSession session){
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginform";
        }

        Board board = boardService.getboard(boardId, false);
        if(board.getUserId() != loginInfo.getUserId()){
            return "redirect:/board?boardId="+boardId; //글보기로 이동.
        }
        //boardId를 숨겨서 보내주고 이에 해당하는 글의 제목과 내용 수정.
        //글쓴이는 수정 가능.
        boardService.updateBoard(boardId, title, content);
        return "redirect:/board?boardId="+boardId;

    }

}


