package com.percyku.learningserver.learningserverspringboot.security;

import com.percyku.learningserver.learningserverspringboot.dao.UserWithOAuth2Dao;
import com.percyku.learningserver.learningserverspringboot.model.Role;
import com.percyku.learningserver.learningserverspringboot.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler  extends SavedRequestAwareAuthenticationSuccessHandler {


    @Autowired
    private UserWithOAuth2Dao userWithOAuth2Dao;


    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {


        OAuth2AuthenticationToken oAuth2AuthenticationToken =(OAuth2AuthenticationToken) authentication;
        String id= "";
        if ("github".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = principal.getAttributes();
//                    attributes.forEach((key, value) -> {
//                        System.out.println(key + "=" + value + " ");
//                    });

            String email = attributes.getOrDefault("email", "").toString();
            String name = attributes.getOrDefault("name", "").toString();

            User user= userWithOAuth2Dao.getUserByEmail(email);
            List<String>roleList =new ArrayList<>();



            if(user!= null){

                if(user.isEnabled()){
                    for(Role tmpRole : user.getRoles()){
                        roleList.add(tmpRole.getName());
                    }
                }

                for(String role : roleList){
                    DefaultOAuth2User newUser = new DefaultOAuth2User(List.of(new SimpleGrantedAuthority(role)),
                            attributes, "id");
                    Authentication securityAuth = new OAuth2AuthenticationToken(newUser, List.of(new SimpleGrantedAuthority(role)),
                            oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                    SecurityContextHolder.getContext().setAuthentication(securityAuth);
                }

            }else{

                System.out.println("user==null");
                List<Role> theRole =new ArrayList<>();
                Role tmpRole = new Role();
                tmpRole.setId(1l);
                tmpRole.setName("ROLE_STUDENT");
                theRole.add(tmpRole);


                user =new User("",
                        "",
                        "",
                        email,
                        "",
                        false);

                userWithOAuth2Dao.createUser(user);


                DefaultOAuth2User newUser = new DefaultOAuth2User(List.of(new SimpleGrantedAuthority(user.getEmail())),
                        attributes, "id");
                Authentication securityAuth = new OAuth2AuthenticationToken(newUser, List.of(new SimpleGrantedAuthority(user.getEmail())),
                        oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                SecurityContextHolder.getContext().setAuthentication(securityAuth);
            }

            id=attributes.getOrDefault("id", "").toString();

        }else if("google".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())){
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = principal.getAttributes();
//                    attributes.forEach((key, value) -> {
//                        System.out.println(key + "=" + value + " ");
//                    });


            String email = attributes.getOrDefault("email", "").toString();
            String name = attributes.getOrDefault("name", "").toString();

            User user= userWithOAuth2Dao.getUserByEmail(email);


            if(user!= null){
                List<String>roleList =new ArrayList<>();
                if(user.isEnabled()){
                    for(Role tmpRole : user.getRoles()){
                        roleList.add(tmpRole.getName());
                    }
                }

                for(String role : roleList){
                    DefaultOAuth2User newUser = new DefaultOAuth2User(List.of(new SimpleGrantedAuthority(role)),
                            attributes, "sub");
                    Authentication securityAuth = new OAuth2AuthenticationToken(newUser, List.of(new SimpleGrantedAuthority(role)),
                            oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                    SecurityContextHolder.getContext().setAuthentication(securityAuth);
                }

            }else{


                Role roleStudent =new Role();
                roleStudent.setId((long) 1);
                roleStudent.setName("ROLE_STUDENT");

                User insertUser =new User(name,"",false);
                insertUser.setEmail(email);
                insertUser.setFirstName( attributes.getOrDefault("given_name", "").toString());
                insertUser.setLastName( attributes.getOrDefault("family_name", "").toString());


                insertUser.setRoles(Arrays.asList(roleStudent));
                userWithOAuth2Dao.createUser(insertUser);



                DefaultOAuth2User newUser = new DefaultOAuth2User(List.of(new SimpleGrantedAuthority("ROLE_STUDENT")),
                        attributes, "sub");
                Authentication securityAuth = new OAuth2AuthenticationToken(newUser, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")),
                        oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                SecurityContextHolder.getContext().setAuthentication(securityAuth);
            }

            id=attributes.getOrDefault("sub", "").toString();
        }

//System.out.println("id:"+id);
        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl(frontendUrl+"?id="+id);
        super.onAuthenticationSuccess(request, response, authentication);
    }

}