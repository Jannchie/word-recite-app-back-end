package com.jannchie.word.controller;

import com.jannchie.word.constant.ResultEnum;
import com.jannchie.word.model.*;
import com.jannchie.word.object.LoginForm;
import com.jannchie.word.security.UserAuthenticationProvider;
import com.jannchie.word.utils.UserUtils;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
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

import static com.jannchie.word.utils.UserUtils.getUsername;
import static com.jannchie.word.utils.UserUtils.setUserWordListInfo;

/**
 * user
 *
 * @author Jannchie
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final MongoTemplate mongoTemplate;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final BCryptPasswordEncoder bcryptPasswordEncoder;

    @Autowired
    UserController(MongoTemplate mongoTemplate, UserAuthenticationProvider userAuthenticationProvider) {
        this.mongoTemplate = mongoTemplate;
        this.userAuthenticationProvider = userAuthenticationProvider;
        this.bcryptPasswordEncoder = new BCryptPasswordEncoder();
    }


    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public ResultResponseEntity<User> login(@Valid @RequestBody LoginForm data) {
        try {
            Authentication request = new UsernamePasswordAuthenticationToken(data.getUsername(),
                    data.getPassword());
            Authentication result = userAuthenticationProvider.authenticate(request);
            SecurityContextHolder.getContext().setAuthentication(result);
        } catch (AuthenticationException e) {
            return new ResultResponseEntity<>(ResultEnum.LOGIN_FAILED);
        }
        return new ResultResponseEntity<>(ResultEnum.LOGIN_SUCCEED, this.getInfo());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/info")
    public User getInfo() {
        User user = UserUtils.getUser();
        LoginForm lf = new LoginForm();
        user.setPassword(null);
        return user;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public ResultResponseEntity<?> create(@Valid @RequestBody LoginForm data) {
        if (0 == mongoTemplate.count(Query.query(Criteria.where("username").is(data.getUsername())), User.class)) {
            mongoTemplate.save(new User(data.getUsername(), this.bcryptPasswordEncoder.encode(data.getPassword())));
        } else {
            return new ResultResponseEntity<>(ResultEnum.USER_ALREADY_EXIST);
        }
        this.login(data);
        return new ResultResponseEntity<>(ResultEnum.SIGN_IN_SUCCEED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/logout")
    public ResultResponseEntity<?> logout() {
        return new ResultResponseEntity<>(ResultEnum.LOGOUT_SUCCEED);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/wordList")
    public List<WordList> listMyWordLists(@RequestParam(name = "p", defaultValue = "1") Long page, @RequestParam(name = "ps", defaultValue = "20") Integer pageSize) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        AggregationResults<WordList> ar = mongoTemplate.aggregate(Aggregation.newAggregation(
                Aggregation.match(Criteria.where("username").is(username)),
                Aggregation.lookup("wordList", "myWordList", "_id", "wordListInfo"),
                Aggregation.unwind("wordListInfo"),
                Aggregation.project().andInclude("wordListInfo._id", "wordListInfo.creator", "wordListInfo.name", "wordListInfo.desc", "wordListInfo.info"),
                Aggregation.skip(((page - 1) * pageSize)),
                Aggregation.limit(pageSize)
        ), User.class, WordList.class);
        List<WordList> result = ar.getMappedResults();
        setUserWordListInfo(username, result);
        return result;
    }


    @RequestMapping(method = RequestMethod.POST, value = "/wordList/{id}")
    public ResultResponseEntity<?> addMyWordList(@PathVariable String id) {
        ObjectId lid = new ObjectId(id);
        String username = getUsername();
        Query q = Query.query(Criteria.where("_id").is(lid));
        if (mongoTemplate.exists(q, WordList.class)) {
            mongoTemplate.updateFirst(Query.query(Criteria.where("username").is(username)), new Update().addToSet("myWordList", lid), User.class);
            return new ResultResponseEntity<>(ResultEnum.SUCCEED, UserUtils.getWordListDataByLid(username,lid));
        } else {
            return new ResultResponseEntity<>(ResultEnum.WORD_LIST_CANNOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/wordList/{id}")
    public ResultResponseEntity<?> delMyWordList(@PathVariable String id) {
        Query q = Query.query(Criteria.where("_id").is(new ObjectId(id)));
        if (mongoTemplate.exists(q, WordList.class)) {
            mongoTemplate.updateFirst(Query.query(Criteria.where("username").is(getUsername())), new Update().pull("myWordList", new ObjectId(id)), User.class);
            return new ResultResponseEntity<>(ResultEnum.SUCCEED);
        } else {
            return new ResultResponseEntity<>(ResultEnum.WORD_LIST_CANNOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/wordList/notReciteWord")
    public List<Word> listNotReciteWords(@RequestParam(name = "id") String id, @RequestParam(name = "size", defaultValue = "20") Integer size) {
        Query q = Query.query(Criteria.where("_id").is(id));
        Set<Integer> recitedWordIds = mongoTemplate.find(Query.query(Criteria.where("username").is(getUsername())), ReciteRecord.class).stream().map(ReciteRecord::getWordId).collect(Collectors.toSet());
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

    @RequestMapping(method = RequestMethod.POST, value = "/word/{id}/master")
    public ResultResponseEntity<?> setMasteredWord(@PathVariable Integer id) {
        mongoTemplate.upsert(Query.query(Criteria.where("username").is(getUsername()).and("wordId").is(id)), Update.update("skillExp", 100).currentDate("lastReciteTime"), ReciteRecord.class);
        return new ResultResponseEntity<>(ResultEnum.SUCCEED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/word/{id}/recite")
    public ResultResponseEntity<?> reciteWord(@PathVariable Integer id) {
        mongoTemplate.upsert(Query.query(Criteria.where("username").is(getUsername()).and("wordId").is(id)),
                new Update().inc("skillExp", 20).currentDate("lastReciteTime"),
                ReciteRecord.class);
        return new ResultResponseEntity<>(ResultEnum.SUCCEED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/word/{id}/forget")
    public ResultResponseEntity<?> forgetWord(@PathVariable Integer id) {
        mongoTemplate.upsert(Query.query(Criteria.where("username").is(getUsername()).and("wordId").is(id)),
                Update.update("skillExp", 0).currentDate("lastReciteTime"),
                ReciteRecord.class);
        return new ResultResponseEntity<>(ResultEnum.SUCCEED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/word/reciting")
    public List<Word> listRecitingWord(@RequestParam(name = "p") Long page, @RequestParam(name = "ps") Long pageSize) {
        MatchOperation match = Aggregation.match(Criteria.where("username").is(getUsername()).and("skillExp").lt(100));
        return getWords(page, pageSize, match);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/word/mastered")
    public List<Word> listMasteredWord(@RequestParam(name = "p") Long page, @RequestParam(name = "ps") Long pageSize) {
        MatchOperation match = Aggregation.match(Criteria.where("username").is(getUsername()).and("skillExp").gte(100));
        return getWords(page, pageSize, match);
    }

    private List<Word> getWords(Long page, Long pageSize, MatchOperation match) {
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        match,
                        Aggregation.skip((page - 1) * pageSize),
                        Aggregation.sort(Sort.by(Sort.Direction.DESC, "lastReciteTime")),
                        Aggregation.limit(pageSize),
                        Aggregation.lookup("word", "wordId", "id", "word"),
                        Aggregation.unwind("word"),
                        Aggregation.replaceRoot("word")
                ), ReciteRecord.class, Word.class
        ).getMappedResults();
    }
}
