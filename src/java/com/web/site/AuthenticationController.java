package com.web.site;

import com.web.config.annotation.WebController;
import com.web.validation.NotBlank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证控制器
 *
 * @author Egan
 * @date 2018/9/2 12:30
 **/
@WebController
public class AuthenticationController {
    private static final Logger log = LogManager.getLogger();
    private static final Map<String, String> userdatabase = new HashMap<>();

    @Inject AuthenticationService authenticationService;

    /**
     * 注销方法
     *
     * @date 2018/9/2 12:31
     * @param  request 用户请求
     * @param session   当前的session
     * @return org.springframework.web.servlet.View 重定向至登录视图
     **/
    @RequestMapping("logout")
    public View logout(HttpServletRequest request, HttpSession session){
        if(log.isDebugEnabled() &&
                request.getUserPrincipal() != null)
            log.debug("User {} logged out.",
                    request.getUserPrincipal().getName());
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

        if(UserPrincipal.getPrincipal(session) != null)
            return this.getTicketRedirect();

        model.put("loginFailed", false);
        model.put("loginForm", new LoginForm());

        return new ModelAndView("/login");
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
                              HttpServletRequest request, @Valid LoginForm form, Errors errors){

        if(UserPrincipal.getPrincipal(session) != null)
            return this.getTicketRedirect();

        if(errors.hasErrors()){
            form.setPassword(null);
            return new ModelAndView("login");
        }

        Principal principal;
        try{
            principal = this.authenticationService.authenticate(
                    form.getUsername(), form.getPassword()
            );
        }catch (ConstraintViolationException e){
            form.setPassword(null);
            return new ModelAndView("login");
        }

        if(principal == null){
            form.setPassword(null);
            model.put("loginFiled", true);
            model.put("loginForm", form);
            return new ModelAndView("login");
        }
        UserPrincipal.setPrincipal(session, principal);
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
    public static class LoginForm
    {
        @NotBlank(message = "{validate.authenticate.username}")
        private String username;
        @NotBlank(message = "{validate.authenticate.password}")
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
