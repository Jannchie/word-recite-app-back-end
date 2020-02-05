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
public class WordList {
    @Id
    private ObjectId id;
    private String name;
    private String desc;
    private List<Integer> wordList;
    private String creator;
    private String creatorId;
    private Info info;

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public WordList() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<Integer> getWordList() {
        return wordList;
    }

    public void setWordList(List<Integer> wordList) {
        this.wordList = wordList;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getLid() {
        return id.toString();
    }

    public static class Info {
        private Integer mastered;
        private Integer reciting;
        private Integer count;

        public Info(Integer count, int mastered, int reciting) {
            this.count = count;
            this.mastered = mastered;
            this.reciting = reciting;
        }

        public Info(int mastered, int reciting) {
            this.mastered = mastered;
            this.reciting = reciting;
        }

        public Info(Integer count) {
            this.count = count;
        }

        public Integer getReciting() {
            return reciting;
        }

        public void setReciting(Integer reciting) {
            this.reciting = reciting;
        }


        public Info() {
        }

        public Integer getMastered() {
            return mastered;
        }

        public void setMastered(Integer mastered) {
            this.mastered = mastered;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }
}
