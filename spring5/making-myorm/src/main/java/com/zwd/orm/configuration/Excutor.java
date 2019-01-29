package com.zwd.orm.configuration;

public interface Excutor {

    public <T> T query(String statement,Object parameter);
}
