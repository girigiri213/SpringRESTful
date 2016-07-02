package com.girigiri.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by JianGuo on 7/2/16.
 */
@Controller
public class IndexController {
    @RequestMapping(value = "")
    public String index() {
        return "index";
    }
}
