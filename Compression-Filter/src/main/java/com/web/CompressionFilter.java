package com.web;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//响应压缩过滤器
public class CompressionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        //检查Accept-Encoding请求头有没有包含gzip编码
        //如果有，Content-Encoding设为GZIP
            //使用私有内部类ResponseWrapper封装PrintWriter或ServletOutputStream，将数据发送到客户端
        //否则提示错误信息

    }

    @Override
    public void destroy() {

    }

    private static class ResponseWrapper extends ServletResponseWrapper
    {

        public ResponseWrapper(HttpServletResponse response)
        {
            super(response);
        }

        //阻止Servlet设置响应的长度头，直到响应被压缩后才能获得内容的长度
    }

    private static class GZIPServletOuputStream extends ServletOutputStream
    {

        GZIPServletOuputStream(ServletOutputStream outputStream)
        {

        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }

        @Override
        public void write(int b) throws IOException {

        }
    }

}
