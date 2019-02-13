package com.zwd.orm.configuration;

public interface Excutor {

     <T> T query(String statement,Object parameter);
}
