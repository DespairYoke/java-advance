package com.zwd.spring.transaction.serviceImpl;

import com.zwd.spring.transaction.dao.AccountDao;
import com.zwd.spring.transaction.dao.StockDao;
import com.zwd.spring.transaction.exception.BuyStockException;
import com.zwd.spring.transaction.service.BuyStockService;

public class BuyStockServiceImpl implements BuyStockService {

    private AccountDao accountDao;
    private StockDao stockDao;

    @Override
    public void addAccount(String accountname, double money) {
        accountDao.addAccount(accountname,money);
    }

    @Override
    public void addStock(String stockname, int amount) {
        stockDao.addStock(stockname,amount);
    }

    @Override
    public void buyStock(String accountname, double money, String stockname, int amount) throws BuyStockException {
        boolean isBuy = true;
        accountDao.updateAccount(accountname, money, isBuy);
        if(isBuy==true){
            throw new BuyStockException("购买股票发生异常");
        }
        stockDao.updateStock(stockname, amount, isBuy);
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public StockDao getStockDao() {
        return stockDao;
    }

    public void setStockDao(StockDao stockDao) {
        this.stockDao = stockDao;
    }

}
