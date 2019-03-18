package com.imooc.spring.web.servlet;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;

/**
 * 异步 Servlet
 *
 * @author 小马哥
 * @since 2018/6/5
 */
@WebServlet(
        asyncSupported = true, // 激活异步特性
        name = "asyncServlet", // Servlet 名字
        urlPatterns = "/async-servlet"
)
public class AsyncServlet extends HttpServlet {

    // 覆盖 service
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // 判断是否支持异步
        if (request.isAsyncSupported()) {
            // 创建 AsyncContext
            AsyncContext asyncContext = request.startAsync();
            // 设置超时时间
            asyncContext.setTimeout(50L);
            asyncContext.addListener(new AsyncListener() {
                @Override
                public void onComplete(AsyncEvent event) throws IOException {
                    println("执行完成");
                }

                @Override
                public void onTimeout(AsyncEvent event) throws IOException {
                    HttpServletResponse servletResponse = (HttpServletResponse)event.getSuppliedResponse();
                    servletResponse.setStatus(SC_SERVICE_UNAVAILABLE);
                    println("执行超时");
                }

                @Override
                public void onError(AsyncEvent event) throws IOException {
                    println("执行错误");
                }

                @Override
                public void onStartAsync(AsyncEvent event) throws IOException {
                    println("开始执行");
                }
            });

            println("Hello,World");
//            ServletResponse servletResponse = asyncContext.getResponse();
//            // 设置响应媒体类型
//            servletResponse.setContentType("text/plain;charset=UTF-8");
//            // 获取字符输出流
//            PrintWriter writer = servletResponse.getWriter();
//            writer.println("Hello,World");
//            writer.flush();

        }
    }

    private static void println(Object object) {
        String threadName = Thread.currentThread().getName();
        System.out.println("AsyncServlet[" + threadName + "]: " + object);
    }
}
