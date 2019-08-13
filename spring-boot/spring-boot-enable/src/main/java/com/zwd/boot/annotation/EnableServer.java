package com.zwd.boot.annotation;

import com.zwd.boot.config.ServerImportRegistrar;
import com.zwd.boot.config.ServerImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//@Import(ServerImportSelector.class)
@Import(ServerImportRegistrar.class)
public @interface EnableServer {

    String value() default "ftpServer";
}
