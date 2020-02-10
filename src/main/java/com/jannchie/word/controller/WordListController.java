package com.jannchie.word.controller;

import com.jannchie.word.constant.ResultEnum;
import com.jannchie.word.model.ResultResponseEntity;
import com.jannchie.word.model.Word;
import com.jannchie.word.model.WordList;
import com.jannchie.word.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Jannchie
 */
@RestController
@RequestMapping(value = "/api/wordList")
public class WordListController {
    @Autowired
    MongoTemplate mongoTemplate;



    @RequestMapping(method = RequestMethod.GET, value = "/create/tag")
    public ResultResponseEntity<?> createToeflList(){
        ArrayList<ArrayList<String>> list = new ArrayList<>();
//        list.add(new ArrayList<>(Arrays.asList("zk","预设中考词汇")));
//        list.add(new ArrayList<>(Arrays.asList("gk","预设高考词汇")));
//        list.add(new ArrayList<>(Arrays.asList("cet4","预设四级词汇")));
//        list.add(new ArrayList<>(Arrays.asList("cet6","预设六级词汇")));
//        list.add(new ArrayList<>(Arrays.asList("gre","预设GRE词汇")));
//        list.add(new ArrayList<>(Arrays.asList("ky","预设考研词汇")));
//        list.add(new ArrayList<>(Arrays.asList("ielts","预设雅思词汇")));
//        list.add(new ArrayList<>(Arrays.asList("toefl","预设托福词汇")));
        list.forEach((a)->{
            WordList wordList =  mongoTemplate.aggregate(Aggregation.newAggregation(
                    Aggregation.match(Criteria.where("tag").is(a.get(0))),
                    Aggregation.group().addToSet("id").as("wordList")
            ),Word.class, WordList.class).getUniqueMappedResult();
            if (wordList == null)
            {
                wordList = new WordList();
            }
            wordList.setDesc("初始生成的词汇书");
            wordList.setCreatorId(UserUtils.getUser().getId().toString());
            wordList.setCreator(UserUtils.getUser().getUsername());
            wordList.setName(a.get(1));
            mongoTemplate.save(wordList);
        });
        return new ResultResponseEntity<>(ResultEnum.SUCCEED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<WordList> listWordList(@RequestParam(name = "p" ,defaultValue = "1") Integer page, @RequestParam(name="ps",defaultValue = "20") Integer pageSize) {
        Query q = new Query().with(PageRequest.of(page - 1,pageSize));
        q.fields().include("name").include("_id").include("desc").include("creator").include("creatorId");
        return mongoTemplate.find(q, WordList.class);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResultResponseEntity<?> saveWordList(@RequestBody WordList wordList) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        wordList.setCreator(authentication.getName());
        mongoTemplate.save(wordList);
        return new ResultResponseEntity<>(ResultEnum.SUCCEED);
    }
}
