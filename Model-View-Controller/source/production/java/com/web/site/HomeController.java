package com.web.site;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import java.time.Instant;
import java.util.Map;

@Controller
public class HomeController {

    @RequestMapping(value = "/user/home", method = RequestMethod.GET)
    /**
     * 返回的User将被添加到模型的特性建currentUser
     **/
    @ModelAttribute("currentUser")
    public User userHome(){
        User user = new User();
        user.setUserId(1234987234L);
        user.setUsername("adam");
        user.setName("Adam JohnSon");
        return user;
    }

    @RequestMapping("/")
    public View home(Map<String, Object> model) {
        model.put("dashboardUrl", "dashboard");
        return new RedirectView("/{dashboardUrl}", true);
    }

    @RequestMapping("/dashboard")
    public String dashboard(Map<String, Object> model){
        model.put("text", "This is a model attribute");
        model.put("date", Instant.now());
        return "home/dashboard";
    }
}
