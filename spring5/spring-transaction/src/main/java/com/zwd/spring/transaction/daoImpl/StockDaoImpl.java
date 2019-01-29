package com.zwd.spring.transaction.daoImpl;

import com.zwd.spring.transaction.dao.StockDao;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class StockDaoImpl extends JdbcDaoSupport implements StockDao {

    @Override
    public void addStock(String sname, int count) {
        String sql = "insert into stock(name,count) values(?,?)";
        this.getJdbcTemplate().update(sql,sname,count);
    }

    @Override
    public void updateStock(String sname, int count, boolean isbuy) {
        String sql = "update stock set count = count-? where name = ?";
        if(isbuy)
            sql = "update stock set count = count+? where name = ?";
        this.getJdbcTemplate().update(sql, count,sname);
    }

}
