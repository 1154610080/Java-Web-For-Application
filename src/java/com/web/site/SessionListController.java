package com.web.site;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * 会话列表控制器
 * 负责提供当前所有会话的信息
 *
 * @author Egan
 * @date 2018/9/13 22:25
 **/
@Controller
@RequestMapping("session")
public class SessionListController {

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Map<String, Object> model){
        model.put("timestamp", System.currentTimeMillis());
        model.put("numberOfSessions", SessionRegistry.getNumberOfSessions());
        model.put("sessionList", SessionRegistry.getAllSessions());
        return "session/list";
    }

}
