package zwd.com.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author zwd
 * @date 2018/10/23 16:18
 * @Email stephen.zwd@gmail.com
 */

@SpringBootApplication
@EnableEurekaClient
public class ClientSever1 {
    public static void main(String[] args) {
        SpringApplication.run(ClientSever1.class);
    }

}
