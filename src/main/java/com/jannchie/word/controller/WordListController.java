package com.jannchie.word.controller;

import com.jannchie.word.constant.ResultEnum;
import com.jannchie.word.model.Result;
import com.jannchie.word.model.Word;
import com.jannchie.word.model.WordList;
import com.jannchie.word.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jannchie
 */
@RestController
@RequestMapping(value = "/api/wordList")
public class WordListController {
    @Autowired
    MongoTemplate mongoTemplate;



    @RequestMapping(method = RequestMethod.GET, value = "/create/toefl")
    public Result createToeflList(){
        WordList wordList =  mongoTemplate.aggregate(Aggregation.newAggregation(
                Aggregation.match(Criteria.where("tag").is("toefl")),
                Aggregation.group().addToSet("id").as("wordList")
        ),Word.class, WordList.class).getUniqueMappedResult();
        if (wordList == null)
        {
            wordList = new WordList();
        }
        wordList.setDesc("无");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        wordList.setCreatorId(UserUtils.getUser().getId().toString());
        wordList.setCreator(UserUtils.getUser().getUsername());
        wordList.setName("托福常用词");
        mongoTemplate.save(wordList);
        return new Result(ResultEnum.SUCCEED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<WordList> listWordList(@RequestParam(name = "p" ,defaultValue = "1") Integer page, @RequestParam(name="ps",defaultValue = "20") Integer pageSize) {
        Query q = new Query().with(PageRequest.of(page - 1,pageSize));
        q.fields().include("name").include("_id").include("desc").include("creator").include("creatorId");
        return mongoTemplate.find(q, WordList.class);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Result saveWordList(@RequestBody WordList wordList) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        wordList.setCreator(authentication.getName());
        mongoTemplate.save(wordList);
        return new Result(ResultEnum.SUCCEED);
    }
}
