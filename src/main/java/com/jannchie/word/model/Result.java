package com.jannchie.word.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jannchie.word.constant.ResultEnum;
import org.springframework.http.ResponseEntity;

/**
 * @author Jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T>{
    private String msg;
    public Result(String msg) {
        this.msg = msg;
    }

    public Result(ResultEnum resultEnum) {
        this.msg = resultEnum.getMsg();
    }
    private T data;
    public Result(ResultEnum resultEnum, T data) {
        this.msg = resultEnum.getMsg();
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
