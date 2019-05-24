package com.example.zwd.springbootcontrolleradvice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.IOException;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-03-19
 **/
@ControllerAdvice(assignableTypes = HelloWorldController.class)
public class HelloWorldControllerAdvice {


    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handle(Throwable throwable) {
        return ResponseEntity.ok(throwable.getMessage());
    }
}
