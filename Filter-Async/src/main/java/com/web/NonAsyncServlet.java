package com.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "nonAsyncServlet", urlPatterns = "/regular")
public class NonAsyncServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        System.out.println("进入 NonAsyncServlet.doGet()。");
        request.getRequestDispatcher("/WEB-INF/jsp/nonAsync.jsp")
                .forward(request, response);
        System.out.println("离开 NonAsyncServlet.doGet()。");
    }
}