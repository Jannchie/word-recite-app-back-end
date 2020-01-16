package com.jannchie.word.controller;

import com.jannchie.word.constant.ResultEnum;
import com.jannchie.word.model.Result;
// import com.jannchie.word.model.User;
import com.jannchie.word.model.User;
import com.jannchie.word.object.LoginForm;
import com.jannchie.word.security.UserAuthenticationProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * user
 */
@RestController
public class UserController {

    private MongoTemplate mongoTemplate;
    private UserAuthenticationProvider userAuthenticationProvider;
    private BCryptPasswordEncoder bcryptPasswordEncoder;
    @Autowired
    UserController(MongoTemplate mongoTemplate, UserAuthenticationProvider userAuthenticationProvider){
        this.mongoTemplate = mongoTemplate;
        this.userAuthenticationProvider = userAuthenticationProvider;
        this.bcryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/login")
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

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/create")
    public Result create(@Valid @RequestBody LoginForm data) {
        if (0 == mongoTemplate.count(Query.query(Criteria.where("username").is(data.getUsername())), User.class)){
            mongoTemplate.save(new User(data.getUsername(), this.bcryptPasswordEncoder.encode(data.getPassword())));
        }else{
            return new Result(ResultEnum.USER_ALREADY_EXIST);
        }
        return new Result(ResultEnum.SIGN_IN_SUCCEED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/logout")
    public Object logout() {
        return new Result(ResultEnum.LOGOUT_SUCCEED);
    }
}
