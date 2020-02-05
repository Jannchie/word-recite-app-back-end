package com.jannchie.word.constant;

import org.springframework.http.HttpStatus;

/**
 * @author Jannchie
 */

public enum ResultEnum {
    /**
     * 操作回复
     */
    SIGN_IN_SUCCEED("注册成功"),
    USER_ALREADY_EXIST("用户名已被占用",HttpStatus.CONFLICT),
    LOGIN_FAILED("登陆失败",HttpStatus.UNAUTHORIZED),
    LOGIN_HINT("需要登录",HttpStatus.UNAUTHORIZED),
    LOGIN_SUCCEED("登录成功"),
    LOGOUT_SUCCEED("登出成功"),
    SUCCEED("操作成功"),
    WORD_LIST_CANNOT_FOUND("未找到单词清单",HttpStatus.NOT_FOUND), USER_NOT_FOUND("未找到该用户",HttpStatus.NOT_FOUND);

    private String msg;
    private HttpStatus code;

    ResultEnum(String msg) {
        this.setMsg(msg);
        this.setCode(HttpStatus.OK);
    }
    ResultEnum(String msg,HttpStatus code ) {
        this.setMsg(msg);
        this.setCode(code);
    }


    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    public HttpStatus getCode() {
        return code;
    }

    public void setCode(HttpStatus code) {
        this.code = code;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

}