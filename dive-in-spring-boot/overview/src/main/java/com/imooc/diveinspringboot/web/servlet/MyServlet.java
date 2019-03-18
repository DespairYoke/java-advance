//package com.imooc.diveinspringboot.web.servlet;
//
//import javax.servlet.AsyncContext;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * My Servlet
// *
// * @author 小马哥
// * @since 2018/5/6
// */
//@WebServlet(urlPatterns = "/my/servlet",
//        asyncSupported=true)
//public class MyServlet extends HttpServlet {
//
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//            throws ServletException, IOException {
//
//        AsyncContext asyncContext = req.startAsync();
//HandlerExceptionResolver
//        asyncContext.start(()->{
//            try {
//                resp.getWriter().println("Hello,World");
//                // 触发完成
//                asyncContext.complete();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//
//    }
//
//
//}
