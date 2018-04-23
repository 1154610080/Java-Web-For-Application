package com.web;

import javax.servlet.*;
import java.io.IOException;

//响应压缩过滤器
public class CompressionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {

    }
}
