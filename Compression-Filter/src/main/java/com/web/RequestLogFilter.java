package com.web;

import org.apache.commons.lang3.time.StopWatch;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

/*
 * 日志过滤器
 * 注意：该过滤器不能正确地处理异步请求
 *
 * @Author Egan
 * @Date 2018/4/24
 **/
class RequestLogFilter implements Filter {

    /*
     * 记录所有访问应用程序的信息
     * 记录格式：IP地址[时间戳] "请求方式 URI 协议" 状态 长度 请求处理的时间
     *
     * @date 2018/4/24 20:39
     * @param [request, response, chain]
     * @return void
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        Instant time = Instant.now();   //请求的时间戳

        StopWatch timer = new StopWatch();

        try {
            timer.start();
            chain.doFilter(request, response);
        }finally {
            timer.stop();
            HttpServletRequest in = (HttpServletRequest)request;
            HttpServletResponse out = (HttpServletResponse)response;
            String length = out.getHeader("Content-Length");

            if (length == null || length.length() == 0) {
                length = "-";
            }

            System.out.println(in.getRemoteAddr() + "[" + time + "] "
            + "\"" + in.getMethod() + " " + in.getRequestURI() + " " + in.getProtocol() + "\" "
            + out.getStatus() + " " + length + " " + timer);
        }

    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }



    @Override
    public void destroy() { }
}
