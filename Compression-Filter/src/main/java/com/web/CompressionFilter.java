package com.web;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

/*
 * 响应压缩过滤器
 * 工作过程：首先，它会检查请求头Accept-Encoding是否包含gzip编码
 * 如果有，Content-Encoding设为GZIP，否则提示错误信息
 * 然后使用私有内部类ResponseWrapper封装PrintWriter或ServletOutputStream，将数据发送到客户端。
 * 该封装对象含有一个GZIPOutputStream的内部实例，响应数据首先被写入GZIPOutputStream，
 * 当请求完成时，它将完成压缩并将响应数据封装到ServletOutputSteam中。
 * ResponseWrapper还将阻止Servlet设置响应的内容长度头，因为直到压缩完成它才能获得内容长度
 *
 * @Author Egan
 * @Date 2018/4/24
 **/
public class CompressionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException
    {

        if(((HttpServletRequest)request).getHeader("Accept-Encoding").contains("gzip")){

            System.out.println("请求已编码");
            ((HttpServletResponse)response).setHeader("Content-Encoding", "gzip");

            ResponseWrapper wrapper = new ResponseWrapper((HttpServletResponse) response);

            try {
                chain.doFilter(request, wrapper);
            }finally {
                try {
                 wrapper.finish();
                }catch (IOException e){
                    e.printStackTrace();
                }

            }

        }else{
            System.out.println("请求未编码");
            chain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {

    }

    private static class ResponseWrapper extends HttpServletResponseWrapper
    {

        private GZIPServletOutputStream outputStream;
        private PrintWriter writer;

        public ResponseWrapper(HttpServletResponse response)
        {
            super(response);
        }

        @Override
        public synchronized ServletOutputStream getOutputStream() throws IOException
        {
            if(writer != null)
                throw new IllegalStateException("getWriter() already called");
            if(outputStream == null)
                outputStream = new GZIPServletOutputStream(super.getOutputStream());

            return outputStream;
        }

        @Override
        public synchronized PrintWriter getWriter() throws IOException
        {
            if(writer == null && outputStream != null)
                throw  new IllegalStateException("getOutputStream() already called");
            if(writer == null){
                outputStream = new GZIPServletOutputStream(super.getOutputStream());
                writer = new PrintWriter(new OutputStreamWriter(
                        this.outputStream, this.getCharacterEncoding()));
            }
            return writer;
        }

        @Override
        public void flushBuffer() throws IOException
        {
            if(writer != null)
                writer.flush();
            if(outputStream != null)
                outputStream.flush();
            super.flushBuffer();
        }

        //阻止其设置响应的内容长度头

        @Override
        public void setContentLength(int len) { }

        @Override
        public void setContentLengthLong(long len) { }

        @Override
        public void setHeader(String name, String value)
        {
            if(!"content-length".equalsIgnoreCase(name))
                super.setHeader(name, value);
        }

        @Override
        public void addHeader(String name, String value)
        {
            if(!"content-length".equalsIgnoreCase(name))
                super.addHeader(name, value);
        }

        @Override
        public void setIntHeader(String name, int value)
        {
            if(!"content-length".equalsIgnoreCase(name))
                super.setIntHeader(name, value);
        }

        @Override
        public void addIntHeader(String name, int value)
        {
            if(!"content-length".equalsIgnoreCase(name))
                super.addIntHeader(name, value);
        }

        public void finish() throws IOException {
            if(writer != null){
                writer.close();
            }else if(outputStream != null){
                outputStream.finish();
            }
        }

    }


    private static class GZIPServletOutputStream extends ServletOutputStream
    {

        private final ServletOutputStream servletOutputStream;
        private final GZIPOutputStream gzipStream;

        public GZIPServletOutputStream(ServletOutputStream outputStream) throws IOException
        {
            servletOutputStream = outputStream;
            gzipStream = new GZIPOutputStream(outputStream);
        }

        @Override
        public boolean isReady()
        {
            return servletOutputStream.isReady();
        }

        @Override
        public void setWriteListener(WriteListener writeListener)
        {
            servletOutputStream.setWriteListener(writeListener);
        }

        @Override
        public void write(int b) throws IOException
        {
            gzipStream.write(b);
        }

        @Override
        public void flush() throws IOException {
            gzipStream.flush();
        }

        @Override
        public void close() throws IOException {
            gzipStream.close();
        }

        public void finish() throws IOException{
            gzipStream.finish();
        }

    }

}
