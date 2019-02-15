package com.hansn.serviceimpl;

import com.hansn.service.TicketStrategy;

public class Children implements TicketStrategy {
    @Override
    public void BuyTicket() {
        System.out.println("1米2以下儿童享受5折优惠");
    }
}
