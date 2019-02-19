package zwd.com.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import zwd.com.zuul.filter.MyFilter;

/**
 * @author zwd
 * @date 2018/10/29 14:40
 * @Email stephen.zwd@gmail.com
 */
@EnableZuulProxy
@SpringBootApplication
public class Gateway {
    public static void main(String[] args) {
        SpringApplication.run(Gateway.class);
    }
    @Bean
    public MyFilter myFilter() {
        return new MyFilter();
    }
}
