package com.zwd.boot.annotation;

import com.zwd.boot.configration.HelloWorldConfigration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(HelloWorldConfigration.class)
public @interface EnableHelloWorld {
}
