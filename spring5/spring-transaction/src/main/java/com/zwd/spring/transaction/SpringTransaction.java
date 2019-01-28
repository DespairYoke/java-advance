package com.zwd.spring.transaction;

import com.zwd.spring.transaction.dao.AccountDao;
import com.zwd.spring.transaction.daoImpl.AccountDaoImpl;
import com.zwd.spring.transaction.exception.BuyStockException;
import com.zwd.spring.transaction.service.BuyStockService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringTransaction {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("transactionProxyFactoryBean.xml");
        BuyStockService buyStockService = (BuyStockService) applicationContext.getBean("serviceProxy");


        try {
            buyStockService.buyStock("小郑", 1000, "知晓科技", 100);
        } catch (BuyStockException e) {
            e.printStackTrace();
        }


    }
}
