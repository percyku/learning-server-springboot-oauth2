package com.percyku.learningserver.learningserverspringboot.dao.imp;

import com.percyku.learningserver.learningserverspringboot.dao.UserWithOAuth2Dao;
import com.percyku.learningserver.learningserverspringboot.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserWithOAuth2DaoImpl implements UserWithOAuth2Dao {
    @Autowired
    private EntityManager entityManager;


        public UserWithOAuth2DaoImpl(EntityManager entityManager){
        this.entityManager=entityManager;
    }
    @Override
    public User getUserByEmail(String email) {
//        String sql = "SELECT member_id, email, password, name, age FROM member WHERE email = :email";
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("email", email);
//
//        List<Member> memberList = namedParameterJdbcTemplate.query(sql, map, memberRowMapper);
//
//        if (memberList.size() > 0) {
//            return memberList.get(0);
//        } else {
//            return null;
//        }

        TypedQuery<User> theQuery = entityManager.createQuery("from User where email=:uEmail", User.class);
        theQuery.setParameter("uEmail", email);

        User theUser = null;
        try {
            theUser = theQuery.getSingleResult();
        } catch (Exception e) {
            theUser = null;
        }

        return theUser;
    }

    @Transactional
    @Override
    public Long createUser(User user) {

        entityManager.merge(user);
        return user.getId();
    }
}
