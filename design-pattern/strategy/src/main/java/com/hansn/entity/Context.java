package com.hansn.entity;

import com.hansn.service.TicketStrategy;

public class Context {
    private TicketStrategy ticketStrategy;

    public Context(TicketStrategy strategy){
        this.ticketStrategy = strategy;
    }

    public void setTicketStrategy(TicketStrategy ticketStrategy) {
        this.ticketStrategy = ticketStrategy;
    }

    public void BuyTicket(){
        this.ticketStrategy.BuyTicket();
    }
}
