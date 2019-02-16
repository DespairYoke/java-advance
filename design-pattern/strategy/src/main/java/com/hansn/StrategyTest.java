package com.hansn;

import com.hansn.entity.Context;
import com.hansn.serviceimpl.Children;
import com.hansn.serviceimpl.Normal;
import com.hansn.serviceimpl.Vip;

public class StrategyTest {
    public static void main(String[] args) {
        System.out.println("普通游客策略：");
        Context context = new Context(new Normal());
        context.BuyTicket();

        System.out.println("年卡VIP游客策略：");
        context.setTicketStrategy(new Vip());
        context.BuyTicket();

        System.out.println("1米2以下儿童策略：");
        context.setTicketStrategy(new Children());
        context.BuyTicket();
    }
}
