package com.web;


import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        urlPatterns = {"/async"},
        name = "asyncServlet",
        asyncSupported = true
)
public class AsyncServlet extends HttpServlet {

    //当前请求的ID
    private volatile int ID = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        final int id;

        synchronized (AsyncServlet.class)
        {
            id = ID++;
        }

        //默认请求超时时间为10s
        long timeout = request.getParameter("timeout") == null ?
                10_000L : Long.parseLong(request.getParameter("timeout"));

        System.out.println("进入 AsyncServlet.doGet(), 请求Id = "
                + id + ",isAsyncStarted = " + request.isAsyncStarted());

        //如果存在unwarp参数，Asycontext将以无参start方法启动，否则有参start
        final AsyncContext context = request.getParameter("unwarp") != null ?
                request.startAsync() : request.startAsync(request, response);

        context.setTimeout(timeout);

        AsyThread thread = new AsyThread(id, context);
        //让Runnable在容器的内部线程池运行更加安全，可以避免资源耗尽
        context.start(thread::doWrok);

        System.out.println("离开 AsyncServlet.doGet(), 请求 Id = "
                + id + ",isAsyncStarted = " + request.isAsyncStarted());
    }

    private static class AsyThread
    {

        private final int id;
        private final AsyncContext context;

        AsyThread(int id, AsyncContext context){
            this.id = id;
            this.context = context;
        }

        public void doWrok()
        {
            System.out.println("异步线程已开始, 请求 id = " + id);

            //5s后响应请求
            try{
                Thread.sleep(5_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            HttpServletRequest request = (HttpServletRequest)context.getRequest();

            System.out.println("线程结束休眠, 请求 id = "
                    + id + ",URL = " + request.getRequestURI() + ".");

            //此时过滤器会拦截jsp内部请求
            this.context.dispatch("/WEB-INF/jsp/async.jsp");

            System.out.println("异步线程已完成, 请求 id = " + id + ".");
        }
    }
}
