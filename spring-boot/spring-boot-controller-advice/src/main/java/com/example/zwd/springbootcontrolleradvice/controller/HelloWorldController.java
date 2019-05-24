package com.example.zwd.springbootcontrolleradvice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-03-19
 **/
@RestController
public class HelloWorldController {

    @GetMapping(value = "/")
    public void index (@RequestParam int a) {
        System.out.println(a);
        System.out.println("HelloWorld");

    }
//
//    @ExceptionHandler(Throwable.class)
//    public ResponseEntity<String> handle(Throwable throwable) {
//        return ResponseEntity.ok(throwable.getMessage());
//    }
}
