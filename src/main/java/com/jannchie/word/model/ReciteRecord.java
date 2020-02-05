package com.jannchie.word.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @author Jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document
public class ReciteRecord {
    @Id
    private ObjectId id;
    private String username;
    private Integer wordId;
    private Integer skillExp;
    private Date lastReciteTime;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getWordId() {
        return wordId;
    }

    public void setWordId(Integer wordId) {
        this.wordId = wordId;
    }

    public Integer getSkillExp() {
        return skillExp;
    }

    public void setSkillExp(Integer skillExp) {
        this.skillExp = skillExp;
    }

    public Date getLastReciteTime() {
        return lastReciteTime;
    }

    public void setLastReciteTime(Date lastReciteTime) {
        this.lastReciteTime = lastReciteTime;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
    public String getRid(){
        return id.toString();
    }
}
