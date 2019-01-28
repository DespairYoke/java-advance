package com.hansn.serviceimpl;

import com.hansn.service.Target;

import java.util.HashMap;
import java.util.Map;

public class Leader implements Target {
    //领导委派员工做具体的事情
    private Map<String,Target> target = new HashMap<String, Target>();
    public Leader(){
        //领导委派员工A和员工B分别做不同的事情
        target.put("打印文件", new ATarget());
        target.put("测试项目", new BTarget());
    }
    @Override
    public void dosomething(String commond) {
        target.get(commond).dosomething(commond);
    }
}
