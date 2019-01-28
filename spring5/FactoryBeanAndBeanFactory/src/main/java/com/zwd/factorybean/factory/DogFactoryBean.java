package com.zwd.factorybean.factory;

import com.zwd.factorybean.domain.Dog;
import org.springframework.beans.factory.FactoryBean;

public class DogFactoryBean implements FactoryBean<Dog> {

    public Dog getObject() throws Exception {
        return new Dog("DogFactoryBean.run");
    }

    public Class<?> getObjectType() {
        return DogFactoryBean.class;
    }

    public boolean isSingleton() {
        return false;
    }
}