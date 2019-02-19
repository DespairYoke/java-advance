package zwd.com.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;


/**
 * @author zwd
 * @date 2018/10/29 11:22
 * @Email stephen.zwd@gmail.com
 */
@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ConfigServer {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServer.class);
    }

}
