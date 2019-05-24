package com.example.zwd.springbootcontrolleradvice.config;

import com.example.zwd.springbootcontrolleradvice.controller.HelloWorldController;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 *  切面配置类 {@link HelloWorldController}
 *
 *
 * @author zwd
 * @since 2019-03-25
 **/
@Configuration
@EnableAspectJAutoProxy
public class AppConfig {


}