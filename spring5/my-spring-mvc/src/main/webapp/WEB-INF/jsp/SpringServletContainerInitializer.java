//package com.zwd.web;
//
//
//import javax.servlet.ServletContainerInitializer;
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.HandlesTypes;
//import java.lang.reflect.Modifier;
//import java.util.Set;
//
///**
// * TODO...
// *
// * @author zwd
// * @since 2019-04-18
// **/
//@HandlesTypes(WebApplicationInitializer.class)
//public class SpringServletContainerInitializer implements ServletContainerInitializer {
//    @Override
//    public void onStartup(Set<Class<?>> webAppInitializerClass, ServletContext ctx) throws ServletException {
//
//        for (Class waiClass : webAppInitializerClass) {
//
//            if (!waiClass.isInterface() && !Modifier.isAbstract(waiClass.getMo difiers()) &&
//            WebApplicationInitializer.class.isAssignableFrom(waiClass)) {
//                try {
//                    WebApplicationInitializer newInstance =(WebApplicationInitializer)waiClass.newInstance();
//                    newInstance.onStartup(ctx);
//                } catch (InstantiationException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//    }
//}
