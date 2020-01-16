package com.jannchie.word.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jannchie.word.constant.ResultEnum;
import com.jannchie.word.model.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    public ResponseEntity<List<Map>> listWords(@RequestParam(name = "p") Integer page,
            @RequestParam(name = "ps") Integer pageSize,
            @RequestParam(name = "kw", defaultValue = "") String keyword) {
        
        return ResponseEntity.ok(mongoTemplate
                .find(new Query(new Criteria().orOperator(Criteria.where("word").is(keyword),
                        Criteria.where("sw").regex(keyword)))
                                .with(PageRequest.of(page - 1, pageSize)),
                        Map.class, "word"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/word")
    public Map searchWord(@RequestParam(name = "word") String word) {
        return mongoTemplate.findOne(Query.query(Criteria.where("word").is(word)), Map.class,
                "word");
    }
}
