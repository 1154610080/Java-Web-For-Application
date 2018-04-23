package com.web;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

//拦截所有请求
public class AnyRequestFilter implements Filter {

    private String name;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        name = filterConfig.getFilterName();
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {

        System.out.println("进入 " + this.name + ".doFilter()");
        filterChain.doFilter(
                new HttpServletRequestWrapper((HttpServletRequest) servletRequest),
                new HttpServletResponseWrapper((HttpServletResponse) servletResponse) {
                });

        System.out.print("离开 " + this.name + ".doFilter()");

        if(servletRequest.isAsyncSupported() && servletRequest.isAsyncStarted()){
            AsyncContext context = servletRequest.getAsyncContext();
            System.out.println(", AsyncContext使用封装请求和响应(有参start) = "
            + !context.hasOriginalRequestAndResponse());
        }else {
            System.out.println("。");
        }
    }

    @Override
    public void destroy() {

    }
}
