package com.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

//    회원가입후 메인 페이지로 돌아간다.
    @GetMapping(value = "/")
    public String main(){
        return "main";
    }
}
