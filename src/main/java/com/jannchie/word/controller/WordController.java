package com.jannchie.word.controller;

import java.util.List;
import java.util.Map;

import com.jannchie.word.model.Word;
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
 */
@RestController
public class WordController {
    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(method = RequestMethod.GET, value = "/api/word/list")
    public ResponseEntity<List<Word>> listWords(@RequestParam(name = "p") Integer page,
                                                @RequestParam(name = "ps") Integer pageSize,
                                                @RequestParam(name = "kw", defaultValue = "") String keyword) {
        List<Word> result = mongoTemplate.find(Query.query(TextCriteria.forDefaultLanguage().matchingAny(keyword.split(" "))).with(PageRequest.of(page, pageSize)), Word.class);
        Word w = mongoTemplate.findOne(Query.query(Criteria.where("word").is(keyword)), Word.class);
        if (w != null) {
            result.add(0, w);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/word/detail")
    public Word searchWord(@RequestParam(name = "word") String word) {
        return mongoTemplate.findOne(Query.query(Criteria.where("word").is(word)), Word.class,
                "word");
    }
}
