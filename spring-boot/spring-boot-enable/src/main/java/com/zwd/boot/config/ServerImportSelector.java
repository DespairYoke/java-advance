package com.zwd.boot.config;

import com.zwd.boot.annotation.EnableServer;
import com.zwd.boot.server.FtpServer;
import com.zwd.boot.server.HttpServer;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;


public class ServerImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {

        MultiValueMap<String, Object> map = metadata.getAllAnnotationAttributes(EnableServer.class.getName());
        String value = (String) map.get("value").get(0);
        if (value.equals("ftpServer")) {
            return new String[]{FtpServer.class.getName()};
        }
        if (value.equals("httpServer")) {
            return new String[]{HttpServer.class.getName()};
        }

        return new String[0];
    }
}
