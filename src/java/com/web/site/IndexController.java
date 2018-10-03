package com.web.site;

import com.web.config.annotation.WebController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 首页导航控制器
 *
 * @author Egan
 * @date 2018/9/13 21:43
 **/
@WebController
public class IndexController {

    @RequestMapping("/")
    public View index(){
        return new RedirectView("/ticket/list", true,false);
    }
}
