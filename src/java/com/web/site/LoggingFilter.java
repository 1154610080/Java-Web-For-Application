package com.web.site;

import org.apache.logging.log4j.ThreadContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

/**
 * 日志过滤器
 *
 *   该过滤器将在请求开始时将标签(id)
 * 和会话用户名(username)添加到ThreadContext中，
 * 并在请求完成时清除ThreadContext，
 * 然后用%X{id}和%X{username}打印出这些属性
 *
 * @author Egan
 * @date 2018/9/1 19:34
 **/

public class LoggingFilter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String id = UUID.randomUUID().toString();
        ThreadContext.put("id", id);
        Principal principal = UserPrincipal.getPrincipal(
                ((HttpServletRequest)request).getSession(false));
        if(principal != null)
            ThreadContext.put("username",
                    principal.getName());

        try{
            ((HttpServletResponse)response).setHeader("X-Wrox-Request-Id", id);
            chain.doFilter(request, response);
        }finally {
            ThreadContext.clearAll();
        }
    }

    @Override
    public void destroy() {

    }
}
