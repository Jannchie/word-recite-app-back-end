package com.jannchie.word.utils;

import com.jannchie.word.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author Jannchie
 */
@Component
public class UserUtils {
    @Autowired
    private static MongoTemplate mongoTemplate;
    public static User getUser(){
        return mongoTemplate.findOne(Query.query(Criteria.where("username").is(getUsername())),User.class);
    }

    public static String  getUsername(){
        return  SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
