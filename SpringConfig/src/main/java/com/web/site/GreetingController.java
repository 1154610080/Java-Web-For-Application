package com.web.site;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GreetingController {

    private GreetingService greetingService;

    @ResponseBody
    @RequestMapping("/")
    public String HelloWorld() {
        return "Hello, World!";
    }

    @ResponseBody
    @RequestMapping(value = "/", params = {"name"})
    public String greetingName(@RequestParam("name") String name){
        return greetingService.getGreeting(name);
    }

    public void setGreetingService(GreetingService greetingService) {
        this.greetingService = greetingService;
    }
}
