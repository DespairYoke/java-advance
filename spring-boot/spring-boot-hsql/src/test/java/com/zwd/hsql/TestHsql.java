package com.zwd.hsql;

import com.zwd.hsql.entity.Demo;
import com.zwd.hsql.service.DaoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootStrap.class)
public class TestHsql {

    @Autowired
    private DaoService daoService;


    @Test
    public void testSave() {
        Demo demoInfo = new Demo();
        demoInfo.setName("张三");
        demoInfo.setAge(20);
        daoService.save(demoInfo);

        demoInfo = new Demo();
        demoInfo.setName("李四");
        demoInfo.setAge(30);
        daoService.save(demoInfo);

        testFind();

        testFindByName();
    }


    public void testFind(){
         List  list = (List<Demo>) daoService.findAll();
         list.stream().forEach(System.out::println);
    }

    public void testFindByName(){
        Demo demo = daoService.findByName("张三");
        System.out.println(demo);
    }

}
