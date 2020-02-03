package com.jannchie.word.controller;

import com.jannchie.word.constant.ResultEnum;
import com.jannchie.word.model.*;
import com.jannchie.word.object.LoginForm;
import com.jannchie.word.security.UserAuthenticationProvider;
import com.jannchie.word.utils.UserUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// import com.jannchie.word.model.User;

/**
 * user
 *
 * @author Jannchie
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private MongoTemplate mongoTemplate;
    private UserAuthenticationProvider userAuthenticationProvider;
    private BCryptPasswordEncoder bcryptPasswordEncoder;

    @Autowired
    UserController(MongoTemplate mongoTemplate, UserAuthenticationProvider userAuthenticationProvider) {
        this.mongoTemplate = mongoTemplate;
        this.userAuthenticationProvider = userAuthenticationProvider;
        this.bcryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public Result login(@Valid @RequestBody LoginForm data) {
        {
            try {
                Authentication request = new UsernamePasswordAuthenticationToken(data.getUsername(),
                        data.getPassword());
                Authentication result = userAuthenticationProvider.authenticate(request);
                SecurityContextHolder.getContext().setAuthentication(result);
            } catch (AuthenticationException e) {
                return new Result(e.getMessage());
            }
        }
        return new Result(ResultEnum.LOGIN_SUCCEED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public Result create(@Valid @RequestBody LoginForm data) {
        if (0 == mongoTemplate.count(Query.query(Criteria.where("username").is(data.getUsername())), User.class)) {
            mongoTemplate.save(new User(data.getUsername(), this.bcryptPasswordEncoder.encode(data.getPassword())));
        } else {
            return new Result(ResultEnum.USER_ALREADY_EXIST);
        }
        return new Result(ResultEnum.SIGN_IN_SUCCEED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/logout")
    public Object logout() {
        return new Result(ResultEnum.LOGOUT_SUCCEED);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/wordList")
    public List<WordList> listMyWordLists(@RequestParam(name = "p", defaultValue = "1") Long page, @RequestParam(name = "ps", defaultValue = "20") Integer pageSize) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        AggregationResults<WordList> ar = mongoTemplate.aggregate(Aggregation.newAggregation(
                Aggregation.match(Criteria.where("username").is(username)),
                Aggregation.lookup("wordList", "myWordList", "_id", "wordListInfo"),
                Aggregation.project().andInclude("wordListInfo._id", "wordListInfo.creator", "wordListInfo.name", "wordListInfo.desc"),
                Aggregation.skip(((page - 1) * pageSize)),
                Aggregation.limit(pageSize)
        ), User.class, WordList.class);
        return ar.getMappedResults();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/wordList")
    public Result addMyWordList(@RequestBody String id) {
        Query q = Query.query(Criteria.where("_id").is(id));
        if (mongoTemplate.exists(q, WordList.class)) {
            mongoTemplate.updateFirst(Query.query(Criteria.where("username").is(UserUtils.getUsername())), new Update().addToSet("myWordList", new ObjectId(id)), User.class);
            return new Result(ResultEnum.SUCCEED);
        } else {
            return new Result(ResultEnum.WORD_LIST_CANNOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/wordList/notReciteWord")
    public List<Word> listNotReciteWords(@RequestParam(name = "id") String id, @RequestParam(name = "size",defaultValue = "20") Integer size) {
        Query q = Query.query(Criteria.where("_id").is(id));
        Set<Integer> recitedWordIds = mongoTemplate.find(Query.query(Criteria.where("username").is(UserUtils.getUsername())), ReciteRecord.class).stream().map(ReciteRecord::getWordId).collect(Collectors.toSet());
        AggregationResults<Word> ar = mongoTemplate.aggregate(Aggregation.newAggregation(
                Aggregation.unwind("wordList"),
                Aggregation.match(Criteria.where("wordList").nin(
                        recitedWordIds
                )),
                Aggregation.lookup("word", "wordList", "id", "word"),
                Aggregation.unwind("word"),
                Aggregation.replaceRoot("word"),
                Aggregation.limit(size)
        ), WordList.class, Word.class);
        return ar.getMappedResults();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/word/master")
    public Result setMasteredWord(@RequestParam(name = "id") Integer id) {
        mongoTemplate.upsert(Query.query(Criteria.where("username").is(UserUtils.getUsername())), Update.update("exp", 100), ReciteRecord.class);
        return new Result(ResultEnum.SUCCEED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/word/recite")
    public Result reciteWord(@RequestParam(name = "id") Integer id) {
        mongoTemplate.upsert(Query.query(Criteria.where("username").is(UserUtils.getUsername()).and("wordId").is(id)),
                new Update().inc("exp", 20).currentDate("lastReciteTime"),
                ReciteRecord.class);
        return new Result(ResultEnum.SUCCEED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/word/forget")
    public Result forgetWord(@RequestParam(name = "id") Integer id) {
        mongoTemplate.upsert(Query.query(Criteria.where("username").is(UserUtils.getUsername()).and("wordId").is(id)),
                new Update().inc("exp", -10).currentDate("lastReciteTime"),
                ReciteRecord.class);
        return new Result(ResultEnum.SUCCEED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/word/reciting")
    public List<Word> listRecitingWord(@RequestParam(name = "p") Long page,@RequestParam(name="ps") Long pageSize) {
        MatchOperation match = Aggregation.match(Criteria.where("username").is(UserUtils.getUsername()).and("exp").lt(100));
        return getWords(page, pageSize, match);
    }
    @RequestMapping(method = RequestMethod.GET, value = "/word/mastered")
    public List<Word> listMasteredWord(@RequestParam(name = "p") Long page,@RequestParam(name="ps") Long pageSize) {
        MatchOperation match = Aggregation.match(Criteria.where("username").is(UserUtils.getUsername()).and("exp").gte(100));
        return getWords(page, pageSize, match);
    }
    private List<Word> getWords( Long page,  Long pageSize, MatchOperation match) {
        return mongoTemplate.aggregate(
                    Aggregation.newAggregation(
                            match,
                            Aggregation.skip((page-1)*pageSize),
                            Aggregation.sort(Sort.by(Sort.Direction.DESC,"lastReciteTime")),
                            Aggregation.limit(pageSize),
                            Aggregation.lookup("word","wordId","id","word"),
                            Aggregation.unwind("word"),
                            Aggregation.replaceRoot("word")
                    ), ReciteRecord.class,Word.class
            ).getMappedResults();
    }
}
