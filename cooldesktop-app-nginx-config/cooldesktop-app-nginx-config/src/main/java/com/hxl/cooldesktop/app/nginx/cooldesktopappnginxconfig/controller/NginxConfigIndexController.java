package com.hxl.cooldesktop.app.nginx.cooldesktopappnginxconfig.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NginxConfigIndexController {
    @GetMapping("/")
    public String index(){
        return "index";
    }
}
