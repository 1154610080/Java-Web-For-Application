package com.web.site;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import java.time.Instant;
import java.util.Map;

@Controller
public class HomeController {

    @RequestMapping("/")
    public View home(Map<String, Object> model) {
        model.put("dashboardUrl", "dashboard");
        return new RedirectView("/{dashboard}", true);
    }

    @RequestMapping("/dashboard")
    public String dashboard(Map<String, Object> model){
        model.put("text", "This is a model attribute");
        model.put("date", Instant.now());
        return "home/dashboard";
    }
}
