package com.example.zwd.springbootswagger2.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class UserController {


    @ApiOperation(value = "接口的功能介绍",notes = "提示接口使用者注意事项",httpMethod = "GET")
    @ApiImplicitParam(dataType = "string",name = "name",value = "姓名",required = true)
    @RequestMapping(value = "/")
    public String index(String name) {

        return "hello "+ name;
    }

//    @RequestMapping(value = "index")
//    public String file(List<MultipartFile> files) {
//
//        System.out.println("sssss");
//        return "zzzzz";
//    }
}
