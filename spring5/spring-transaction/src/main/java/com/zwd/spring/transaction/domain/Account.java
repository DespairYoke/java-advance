package com.zwd.spring.transaction.domain;

/**
 * 账户对象
 *
 */
public class Account {

    private int accountid;
    private String name;
    private int balance;


    public int getAccountid() {
        return accountid;
    }
    public void setAccountid(int accountid) {
        this.accountid = accountid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getBalance() {
        return balance;
    }
    public void setBalance(int balance) {
        this.balance = balance;
    }
}
