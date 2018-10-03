package com.web.site;

import com.web.config.annotation.WebController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import java.util.Map;

/**
 * 会话列表控制器
 * 负责提供当前所有会话的信息
 *
 * @author Egan
 * @date 2018/9/13 22:25
 **/
@WebController
@RequestMapping("session")
public class SessionListController {

    @Inject SessionRegistry sessionRegistry;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Map<String, Object> model){
        model.put("timestamp", System.currentTimeMillis());
        model.put("numberOfSessions", sessionRegistry.getNumberOfSessions());
        model.put("sessionList", sessionRegistry.getAllSessions());
        return "session/list";
    }

}
