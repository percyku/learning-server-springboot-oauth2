package com.percyku.learningserver.learningserverspringboot.controller;


import com.percyku.learningserver.learningserverspringboot.dto.UserRegisterRequest;
import com.percyku.learningserver.learningserverspringboot.util.Member;
import com.percyku.learningserver.learningserverspringboot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Pattern;

@Validated
@RestController
public class UserController {

    @Autowired
    UserService userService;


    @PostMapping("/register")
    public ResponseEntity<Member> register(@RequestBody @Valid UserRegisterRequest member){

        long result = userService.createUser(member);
        if(result == -1){
            throw new CommonException("This user had been exist: "+member.getEmail());
        }
        Member theMember =userService.getMemberById(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(theMember);
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<String> updateProfile(HttpServletRequest request, HttpServletResponse response,
                            Authentication authentication, @RequestBody @Valid UserRegisterRequest member){


//
        String userEmail ="";
        ///^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/;
        //^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
        //if(authentication.getName().matches("/^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$/;")){
        String regex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        Pattern pattern =Pattern.compile(regex);
      if(pattern.matcher(authentication.getName()).find()){
            userEmail= authentication.getName();
        }else{
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes =principal.getAttributes();
            userEmail =attributes.get("email").toString();

        }



        Long userId =  userService.update(userEmail,member);

        if(userId==-1){
            throw new CommonException("This user had been exist: "+member.getEmail());
        }

        if(userId==-2){
            throw new CommonException("We cannot find this User: "+userEmail);
        }

        CookieClearingLogoutHandler cookies = new CookieClearingLogoutHandler("JSESSIONID","XSRF-TOKEN");
        cookies.logout(request,response,authentication);

        return ResponseEntity.status(HttpStatus.OK).body("update successful");
    }

    @GetMapping("/loginforlearnsys")
    public ResponseEntity<Member> loginforlearnsys(Authentication authentication,
                                                   @RequestParam(value="role", required=true) String role
                                                   ){
        String username = authentication.getName();
        Member theMember = userService.getMemberByEmail(username);

        if(theMember.getRoles().contains(role)){
            theMember.setRole(role.split("_")[1]);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(theMember);
    }


    @GetMapping("/loginforlearnsyswithauth2")
    public ResponseEntity<Member> loginforlearnsysWithAuth2(Authentication authentication,@RequestParam(value="id", required=true)String id){
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes =principal.getAttributes();
        Member theMember =null;


        if(attributes.get("id") != null?attributes.get("id").toString().equals(id):attributes.get("sub").toString().equals(id)){
            theMember = userService.getMemberByEmail(attributes.get("email").toString());
            if(theMember.isEnable()){
                theMember.setRole(theMember.getRoles().get(0).split("_")[1]);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(theMember);
    }


    @GetMapping("/logoutSuccess")
    public ResponseEntity<String> logoutSuccess(){
        return  ResponseEntity.status(HttpStatus.OK).body("logout");
    }


}
