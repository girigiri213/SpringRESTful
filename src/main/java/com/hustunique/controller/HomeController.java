package com.hustunique.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by JianGuo on 6/21/16.
 */
@Controller
public class HomeController {
    @RequestMapping("/")
    public String hello() {
        return "hello";
    }
}
