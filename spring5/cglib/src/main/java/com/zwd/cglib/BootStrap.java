package com.zwd.cglib;

import com.zwd.cglib.dao.Dao;
import com.zwd.cglib.proxy.DaoProxy;
import net.sf.cglib.proxy.Enhancer;

/**
 * @author zwd
 * @since 2019-08-12
 **/
public class BootStrap {

    public static void main(String[] args) {

        DaoProxy daoProxy = new DaoProxy();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Dao.class);
        enhancer.setCallback(daoProxy);

        Dao dao = (Dao) enhancer.create();
        dao.update();
        dao.select();
    }
}
