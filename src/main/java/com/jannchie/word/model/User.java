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
public class User {
    @Id
    private ObjectId id;
    private String username;
    private String password;
    private Integer exp;
    private List<WordList> myWordList;
    private List<ReciteStatistic> reciteStatistics;
    public String getUid(){
        return id.toString();
    }

    public ObjectId getId() {
        return id;
    }

    public List<WordList> getMyWordList() {
        return myWordList;
    }

    public void setMyWordList(List<WordList> myWordList) {
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
    private Settings settings;

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public static class Settings{
        private Boolean dark;
        private Boolean autoPlayAudio;
        private Integer wordsOfRound;

        public Settings(Boolean dark, Boolean autoPlayAudio, Integer wordsOfRound) {
            this.dark = dark;
            this.autoPlayAudio = autoPlayAudio;
            this.wordsOfRound = wordsOfRound;
        }

        public Settings() {
        }

        public Boolean getDark() {
            return dark;
        }

        public void setDark(Boolean dark) {
            this.dark = dark;
        }

        public Boolean getAutoPlayAudio() {
            return autoPlayAudio;
        }

        public void setAutoPlayAudio(Boolean autoPlayAudio) {
            this.autoPlayAudio = autoPlayAudio;
        }

        public Integer getWordsOfRound() {
            return wordsOfRound;
        }

        public void setWordsOfRound(Integer wordsOfRound) {
            this.wordsOfRound = wordsOfRound;
        }
    }

    public List<ReciteStatistic> getReciteStatistics() {
        return reciteStatistics;
    }

    public void setReciteStatistics(List<ReciteStatistic> reciteStatistics) {
        this.reciteStatistics = reciteStatistics;
    }

    public static class ReciteStatistic {
        private Integer mastered;
        private Integer reciting;
        private Date updateDate;

        public Date getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(Date updateDate) {
            this.updateDate = updateDate;
        }

        public Integer getMastered() {
            return mastered;
        }

        public void setMastered(Integer mastered) {
            this.mastered = mastered;
        }

        public Integer getReciting() {
            return reciting;
        }

        public void setReciting(Integer reciting) {
            this.reciting = reciting;
        }
    }
}
