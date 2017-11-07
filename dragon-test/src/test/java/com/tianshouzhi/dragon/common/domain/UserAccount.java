package com.tianshouzhi.dragon.common.domain;

/**
 * Created by TIANSHOUZHI336 on 2017/3/29.
 */
public class UserAccount {
    private Integer userId;
    private String accountNo;
    private Double money;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "userId=" + userId +
                ", accountNo='" + accountNo + '\'' +
                ", money=" + money +
                '}';
    }
}
