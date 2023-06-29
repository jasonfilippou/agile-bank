package com.agilebank.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bankapi")
@CrossOrigin
public class HelloWorldController {

    @GetMapping("/hello")
    public String firstPage(){
        return "Hello World!";
    }
}
