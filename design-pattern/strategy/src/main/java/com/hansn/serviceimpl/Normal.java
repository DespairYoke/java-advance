package com.hansn.serviceimpl;

import com.hansn.service.TicketStrategy;

public class Normal implements TicketStrategy {
    @Override
    public void BuyTicket() {
        System.out.println("普通游客没有优惠");
    }
}
