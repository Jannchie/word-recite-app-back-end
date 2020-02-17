package com.jannchie.word.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastReciteTime;
    private Word word;

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

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
