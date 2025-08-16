package com.example.board.controller;

import com.example.board.dto.LoginInfo;
import com.example.board.dto.User;
import com.example.board.service.Userservice;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final Userservice userservice;

    //http://localhost:8081/userRegForm
    //classpath:/templates/userRegForm.html

    @GetMapping("/userRegForm")
    public String userRegform(){
        return "userRegForm";
    }

    @PostMapping("/userReg")
    public String userReg(
            @RequestParam("id") String id,
            @RequestParam("password") String password,
            @RequestParam("password_check") String password_check,
            @RequestParam("nickname") String nickname,
            @RequestParam("email") String email
    ){
        System.out.println("id : "+id);
        System.out.println("password : "+password);
        System.out.println("password_check : "+password_check);
        System.out.println("nickname : "+nickname);
        System.out.println("email : "+email);

        userservice.addUser(email, id, password, nickname);

        return "redirect:welcome"; //이부분 메인 페이지로 이동하는 방향으로 갈 수도 있으니 바꿀 때 주의하기. 브라우저에게 자동으로 http://localhost:8081/welcome으로 이동.
    }

    //http://localhost:8081/welcome
    @GetMapping("/welcome")
    public String welcome(){
        return "welcome";
    }

    //http://localhost:8081/loginform
    @GetMapping("/loginform")
    public String loginform(){
        return "loginform";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("id") String id,
            @RequestParam("password") String password,
            HttpSession httpSession //spring이 자동으로 session처리하는 HttpSession객체를 넣어줌.
    ) {
        //아이디에 해당하는 회원 정보 읽어온 후 아이디 암호가 맞다면 세션에 회원정보 저장
        System.out.println("id: " + id);
        System.out.println("password: " + password);

        User user = null;

        try {
            user = userservice.getUser(id);
            if(user.getPassword().equals(password)){
                System.out.println("암호가 같습니다.");
                LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getId(),user.getNickname());

                // 권한 정보를 읽어와서 logininfo에 추가.

                List<String> roels = userservice.getRoles(user.getUserId());
                loginInfo.setRoles(roels);

                httpSession.setAttribute("loginInfo",loginInfo); //첫번쨰 파라미터가 키, 두번째 파라미터가 값.
                System.out.println("세션에  로그인 정보가 저장된다.");
            }else{
                throw new RuntimeException("암호가 일치하지 않음.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "redirect:/loginform?error=true";
        }
        return "redirect:/share";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession){
        httpSession.removeAttribute("loginInfo");
        //세션 정보 삭제
        return "redirect:/share";
    }
}
