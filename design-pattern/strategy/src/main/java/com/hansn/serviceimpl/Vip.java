package com.hansn.serviceimpl;

import com.hansn.service.TicketStrategy;

public class Vip implements TicketStrategy {
    @Override
    public void BuyTicket() {
        System.out.println("办年卡游客享受8折优惠");
    }
}
