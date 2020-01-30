package com.jannchie.word.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author Jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document
public class User {
    @Id
    private ObjectId id;
    private String username;
    private String password;
    private Integer exp;
    private List<ObjectId> myWordList;
    public String getUid(){
        return id.toString();
    }

    public ObjectId getId() {
        return id;
    }

    public List<ObjectId> getMyWordList() {
        return myWordList;
    }

    public void setMyWordList(List<ObjectId> myWordList) {
        this.myWordList = myWordList;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.exp = 0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
