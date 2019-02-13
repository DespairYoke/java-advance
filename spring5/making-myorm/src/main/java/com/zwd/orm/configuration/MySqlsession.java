package com.zwd.orm.configuration;

import java.lang.reflect.Proxy;

public class MySqlsession {

    private Excutor excutor= new MyExcutor();

    private MyConfiguration myConfiguration = new MyConfiguration();

    public MySqlsession(String configpath) {

        myConfiguration.getAllMapper(configpath);
    }



    public <T> T selectOne(String statement,Object parameter){
        return excutor.query(statement, parameter);
    }

    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> clas){
        //动态代理调用
        return (T) Proxy.newProxyInstance(clas.getClassLoader(),new Class[]{clas},
                new MyMapperProxy(myConfiguration,this));
    }

}
