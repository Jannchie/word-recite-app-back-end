package com.jannchie.word.model;

import com.jannchie.word.constant.ResultEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Jannchie
 */
public class ResultResponseEntity<T> extends ResponseEntity<Result<T>> {

    public ResultResponseEntity(ResultEnum resultEnum) {
        super(new Result<>(resultEnum), resultEnum.getCode());
    }

    public ResultResponseEntity(ResultEnum resultEnum, T data) {
        super(new Result<>(resultEnum,data), resultEnum.getCode());
    }
}
