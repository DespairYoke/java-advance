package com.zwd.factorybean;

import com.zwd.factorybean.domain.Dog;
import com.zwd.factorybean.factory.DogFactoryBean;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestMain {

    @Test
    public void test() throws Exception {

        ApplicationContext ctx =new ClassPathXmlApplicationContext("bean.xml");

        Dog dog = (Dog) ctx.getBean("dog");
        dog.run();

        Dog dog1 = (Dog) ctx.getBean("dogFactoryBean");
        dog1.run();

        DogFactoryBean docFactoryBean1 = (DogFactoryBean) ctx.getBean("&dogFactoryBean");
        Dog dog2 = docFactoryBean1.getObject();
        dog2.run();
    }
}
