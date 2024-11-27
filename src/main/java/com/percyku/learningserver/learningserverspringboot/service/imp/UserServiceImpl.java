package com.percyku.learningserver.learningserverspringboot.service.imp;

import com.percyku.learningserver.learningserverspringboot.dao.UserDao;
import com.percyku.learningserver.learningserverspringboot.dto.UserRegisterRequest;
import com.percyku.learningserver.learningserverspringboot.util.Member;
import com.percyku.learningserver.learningserverspringboot.model.Role;
import com.percyku.learningserver.learningserverspringboot.model.User;
import com.percyku.learningserver.learningserverspringboot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public Long createUser(UserRegisterRequest member) {
        User user = userDao.getUserByEmail(member.getEmail());

        if(user!= null){
            return (long)-1;
        }


        List<Role> theRole =new ArrayList<>();
        boolean enabled=false;
        if(member.getRoles()!= null){
            for(String roleName : member.getRoles()) {
                Role tmpRole = new Role();
                long roleId = 0;
                if ("ROLE_STUDENT".equals(roleName)) {
                    roleId = 1;
                } else if ("ROLE_INSTRUCTOR".equals(roleName)) {
                    roleId = 2;
                } else if ("ROLE_ADMIN".equals(roleName)) {
                    roleId = 3;
                }
                tmpRole.setId(roleId);
                tmpRole.setName(roleName);
                theRole.add(tmpRole);
            }
            enabled=true;
        }
        String hashedPassword = passwordEncoder.encode(member.getPassword());
        if(enabled){
            user =new User(member.getUsername(),
                    member.getFirst_name(),
                    member.getLast_name(),
                    member.getEmail(),
                    hashedPassword,
                    enabled,
                    theRole);
        }else {
            user =new User(member.getUsername(),
                    member.getFirst_name(),
                    member.getLast_name(),
                    member.getEmail(),
                    hashedPassword,
                    enabled);
        }
        log.debug(user.toString());



        return userDao.createUser(user);
    }


    @Override
    @Transactional
    public Long update(   String userEmail ,UserRegisterRequest updateMember) {
        User user = userDao.getUserByEmail(userEmail);

        if(user == null){
            return (long)-2;
        }

        if(!userEmail.equals(updateMember.getEmail())){
            if(userDao.getUserByEmail(updateMember.getEmail())!=null){
                log.debug("Exist update mail:"+updateMember.getEmail());
                return (long)-1;
            }else{
                user.setEmail(updateMember.getEmail());
            }
        }
        user.setUserName(updateMember.getUsername());
        user.setLastName(updateMember.getLast_name());
        user.setFirstName(updateMember.getFirst_name());

        if(updateMember.getPassword()!=null && !updateMember.getPassword().equals("Empty123")){
            user.setPassword(passwordEncoder.encode(updateMember.getPassword()));
        }

        List<Role> theRole =new ArrayList<>();
        boolean enabled=false;
        if(updateMember.getRoles()!= null){
            for(String roleName : updateMember.getRoles()) {
                Role tmpRole = new Role();
                log.debug("roleName:"+roleName);
                long roleId = 0;
                if ("ROLE_STUDENT".equals(roleName)) {
                    roleId = 1;
                } else if ("ROLE_INSTRUCTOR".equals(roleName)) {
                    roleId = 2;
                } else if ("ROLE_ADMIN".equals(roleName)) {
                    roleId = 3;
                }
                tmpRole.setId(roleId);
                tmpRole.setName(roleName);
                theRole.add(tmpRole);
            }
            enabled=true;
        }

        if(!user.isEnabled()){
            log.debug("!user.isEnabled()");
            user.setEnabled(true);
            user.setRoles(theRole);
            return userDao.update(user);

        }
        else{
            log.debug("user.isEnabled()");
        }





        return user.getId();
    }

    @Override
    public Member getMemberById(Long memberId) {
        User user =userDao.getUserById(memberId);
        Member member =new Member();

        member.setEmail(user.getEmail());
        member.setFirst_name(user.getFirstName());
        member.setLast_name(user.getLastName());
        member.setUsername(user.getUserName());

        List<String>theRole =new ArrayList<>();
        if(user.isEnabled()){
            for(Role tmpRole : user.getRoles()){
                theRole.add(tmpRole.getName());
            }
        }
        member.setRoles(theRole);

        member.setEnable(user.isEnabled());
        return member;
    }

    @Override
    public Member getMemberByEmail(String email) {
        User user =userDao.getUserByEmail(email);
        Member member =new Member();

        member.setId(user.getId());
        member.setEmail(user.getEmail());
        member.setFirst_name(user.getFirstName());
        member.setLast_name(user.getLastName());
        member.setUsername(user.getUserName());

        List<String>theRole =new ArrayList<>();
        if(user.isEnabled()){
            for(Role tmpRole : user.getRoles()){
                theRole.add(tmpRole.getName());
            }
        }
        member.setRoles(theRole);
        member.setEnable(user.isEnabled());

        return member;

    }
}
