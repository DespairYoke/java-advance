package com.zwd.boot.config;

import com.zwd.boot.annotation.EnableServer;
import com.zwd.boot.server.FtpServer;
import com.zwd.boot.server.HttpServer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.MultiValueMap;

public class ServerImportRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        MultiValueMap<String, Object> map = metadata.getAllAnnotationAttributes(EnableServer.class.getName());
        String value = (String) map.get("value").get(0);
        if (value.equals("ftpServer")) {

            ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);

            TypeFilter typeFilter = new AssignableTypeFilter(FtpServer.class);
            scanner.addIncludeFilter(typeFilter);
            scanner.scan("com.zwd.boot.server");

        }
        if (value.equals("httpServer")) {
            ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);

            TypeFilter typeFilter = new AssignableTypeFilter(HttpServer.class);
            scanner.addIncludeFilter(typeFilter);

        }
    }
}
