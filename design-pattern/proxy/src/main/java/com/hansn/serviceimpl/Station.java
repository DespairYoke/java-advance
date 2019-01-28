package com.hansn.serviceimpl;

import com.hansn.service.TicketService;

public class Station implements TicketService {
    @Override
    public void sellTicket() {
        System.out.println("售票");
    }
    @Override
    public void Consultation() {
        System.out.println("咨询");
    }
    @Override
    public void ReturnTicket() {
        System.out.println("退票");
    }
}
