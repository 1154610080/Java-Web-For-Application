package com.web.site;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

/**
 * 验证过滤器
 *
 *   用于简化认证，该过滤器将在所有HTTP方法的请求上执行认证检查，
 * 若用户未登录，则将用户重定向至登录界面
 *
 * @author Egan
 * @date 2018/9/2 12:06
 **/
public class AuthenticationFilter implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpSession session = ((HttpServletRequest)request).getSession(false);
        Principal principal = UserPrincipal.getPrincipal(session);

        if(principal == null)
            ((HttpServletResponse)response).sendRedirect(
                    ((HttpServletRequest)request).getContextPath() + "/login");
        else{

            chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) request){
                @Override
                public Principal getUserPrincipal() {
                    return principal;
                }
            }, response);
        }

    }

    @Override
    public void destroy() {

    }
}
