package com.jannchie.word.model;

import com.jannchie.word.constant.ResultEnum;

/**
 * @author Jannchie
 */
public class Result {
    private String msg;
    public Result(String msg) {
        this.msg = msg;
    }

    public Result(ResultEnum loginFaild) {
        this.msg = loginFaild.getMsg();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
