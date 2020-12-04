package com.kctv.api.controller.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/sleep")
    public String pause() throws InterruptedException{
        Thread.sleep(10000);
        return "process finished";
    }
}
