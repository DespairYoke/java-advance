package zwd.com.client.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zwd
 * @date 2018/10/29 14:55
 * @Email stephen.zwd@gmail.com
 */
@RestController
public class HelloController {

    @Value(value = "${zwd.name}")
    private String name;
    @RequestMapping(value = "/hello")
    public String helloworld() {
        return "hello client server2 "+name;
    }
}
