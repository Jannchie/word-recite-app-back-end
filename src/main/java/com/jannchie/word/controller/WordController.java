package com.jannchie.word.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.jannchie.word.model.ReciteRecord;
import com.jannchie.word.model.Word;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * user
 * @author Jannchie
 */
@RestController
@RequestMapping(value = "/api/word")
public class WordController {
    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public ResponseEntity<List<ReciteRecord>> listWords(@RequestParam(name = "p") Integer page,
                                                        @RequestParam(name = "ps") Integer pageSize,
                                                        @RequestParam(name = "kw", defaultValue = "") String keyword) {
        List<Word> result = mongoTemplate.find(Query.query(TextCriteria.forDefaultLanguage().matchingAny(keyword.split(" "))).with(PageRequest.of(page, pageSize)), Word.class);
        Word w = mongoTemplate.findOne(Query.query(Criteria.where("word").is(keyword)), Word.class);
        if (w != null) {
            result.add(0, w);
        }
        List<ReciteRecord> reciteRecords = result.stream().map((word)->{
            ReciteRecord reciteRecord =  new ReciteRecord();
            reciteRecord.setWord(word);
            reciteRecord.setLastReciteTime(null);
            reciteRecord.setId(ObjectId.get());
            reciteRecord.setSkillExp(0);
            return reciteRecord;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(reciteRecords);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/detail")
    public Word searchWord(@RequestParam(name = "word") String word) {
        return mongoTemplate.findOne(Query.query(Criteria.where("word").is(word)), Word.class,
                "word");
    }
}
