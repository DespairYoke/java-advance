package com.zwd.hsql.service;

import com.zwd.hsql.entity.Demo;
import org.springframework.data.repository.CrudRepository;


public interface DaoService extends CrudRepository<Demo,Long> {

    Demo findByName(String name);

}
