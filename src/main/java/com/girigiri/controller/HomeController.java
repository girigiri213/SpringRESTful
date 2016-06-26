package com.girigiri.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by JianGuo on 6/21/16.
 */
@RestController
public class HomeController {
    @RequestMapping("/")
    public String hello() {
        return "hello";
    }
}
