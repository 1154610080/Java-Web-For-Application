package com.web.site;

public class GreetingServiceImpl implements GreetingService {

    @Override
    public String getGreeting(String name) {
        return "你好，" + name;
    }
}
