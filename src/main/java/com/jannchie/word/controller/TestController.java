package com.jannchie.word.controller;

import com.jannchie.word.constant.ResultEnum;
import com.jannchie.word.model.Result;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * user
 */
@RestController
public class TestController {
    @RequestMapping(method = RequestMethod.GET, value = "/test/hello-world")
    public ResponseEntity<Result> helloWorld() {
        return ResponseEntity.ok(new Result("Hello World!"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/home")
    public ResponseEntity<Result> home() {
        return ResponseEntity.ok(new Result("首页"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public ResponseEntity<Result> login() {
        return ResponseEntity.ok(new Result(ResultEnum.LOGIN_HINT));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/no-login")
    public ResponseEntity<Result> postLogin() {
        return ResponseEntity.ok(new Result(ResultEnum.LOGIN_HINT));
    }

}