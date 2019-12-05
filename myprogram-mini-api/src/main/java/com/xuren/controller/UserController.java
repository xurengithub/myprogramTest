package com.xuren.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/")
public class UserController {

    @RequestMapping("list")
    public Map<String,String> list() {
        Map<String,String> m = new HashMap<>();
        m.put("name", "xuren");
        return m;
    }
}
