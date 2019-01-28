package com.zwd.spring.transaction.daoImpl;

import com.zwd.spring.transaction.dao.AccountDao;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AccountDaoImpl extends JdbcDaoSupport implements AccountDao {

    @Override
    public void addAccount(String name, double money) {
        String sql = "insert account(name,balance) values(?,?);";
        this.getJdbcTemplate().update(sql,name,money);

    }

    @Override
    public void updateAccount(String name, double money, boolean isbuy) {
        String sql = "update account set balance=balance+? where name=?";
        if(isbuy)
            sql = "update account set balance=balance-? where name=?";
        this.getJdbcTemplate().update(sql, money,name);
    }

}
