//package com.imooc.web.servlet.support;
//
//import com.imooc.web.config.DispatcherServletConfiguration;
//import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
//
///**
// * Spring Web MVC 自动装配 默认实现
// *
// * @author 小马哥
// * @since 2018/5/21
// */
//public class DefaultAnnotationConfigDispatcherServletInitializer extends
//        AbstractAnnotationConfigDispatcherServletInitializer {
//    @Override
//    protected Class<?>[] getRootConfigClasses() { // web.xml
//        return new Class[0];
//    }
//
//    @Override
//    protected Class<?>[] getServletConfigClasses() { // DispatcherServlet
//        return new Class[]{DispatcherServletConfiguration.class};
//    }
//
//    @Override
//    protected String[] getServletMappings() {
//        return new String[]{"/"};
//    }
//}
