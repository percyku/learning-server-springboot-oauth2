package com.percyku.learningserver.learningserverspringboot.dao;

import com.percyku.learningserver.learningserverspringboot.model.User;

public interface UserWithOAuth2Dao {


    User getUserByEmail(String email);

    Long createUser(User user);
}
