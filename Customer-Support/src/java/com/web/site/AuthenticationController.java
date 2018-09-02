package com.web.site;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证控制器
 *
 * @author Egan
 * @date 2018/9/2 12:30
 **/
@Controller
public class AuthenticationController {
    private static final Logger log = LogManager.getLogger();
    private static final Map<String, String> userdatabase = new HashMap<>();

    static {
        userdatabase.put("Nicholas", "password");
        userdatabase.put("Sarah", "drowssap");
        userdatabase.put("Mike", "wordpass");
        userdatabase.put("John", "green");
    }

    /**
     * 注销方法
     *
     * @date 2018/9/2 12:31
     * @param session   当前的session
     * @return org.springframework.web.servlet.View 重定向至登录视图
     **/
    @RequestMapping("logout")
    public View logout(HttpSession session){
        if(log.isDebugEnabled())
            log.debug("User {} logged out.",
                    session.getAttribute("username"));
        session.invalidate();

        return new RedirectView("/login", true, false);
    }

    /**
     * 用户登录视图
     *
     * @date 2018/9/2 12:50
     * @param model 通用模型
	 * @param session 当前session
     * @return org.springframework.web.servlet.ModelAndView
     **/
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public ModelAndView login(Map<String, Object>model, HttpSession session){
        if(session.getAttribute("username") != null)
            return this.getTicketRedirect();

        model.put("loginFailed", false);
        model.put("loginForm", new Form());

        return new ModelAndView("login");
    }

    /**
     * 用户登录方法
     *
     * @date 2018/9/2 12:34
     * @param model 通用模型
	 * @param session  当前session
     * @param request HTTP请求
     * @param form 提交表单
     * @return org.springframework.web.servlet.ModelAndView 重定向视图
     **/
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ModelAndView login(Map<String, Object>model, HttpSession session,
                              HttpServletRequest request, Form form){
        if(session.getAttribute("username") != null)
            return this.getTicketRedirect();

        if(form.getUsername() == null || form.getPassword() == null ||
                !userdatabase.containsKey(form.getUsername()) ||
                !form.getPassword().equals(userdatabase.get(form.getUsername()))){
            log.warn("Login failed for user {}.", form.getUsername());
            form.setPassword(null);
            model.put("loginFailed", true);
            model.put("loginForm", form);
            return new ModelAndView("login");
        }

        log.debug("User {} successfully logged in.", form.getUsername());
        session.setAttribute("username", form.getUsername());
        request.changeSessionId();
        return this.getTicketRedirect();

    }

    private ModelAndView getTicketRedirect(){
        return new ModelAndView(new RedirectView(
                "/ticket/list", true, false));
    }

    /**
     * 表单POJO
     *
     * @author Egan
     * @date 2018/9/2 12:38
     **/
    private static class Form
    {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
