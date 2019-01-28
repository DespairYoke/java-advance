package com.hansn;

import com.hansn.proxy.StationProxy;
import com.hansn.serviceimpl.Station;

public class ProxyTest {
    public static void main(String[] args) {
        Station station = new Station();
        StationProxy stationProxy = new StationProxy(station);
        stationProxy.sellTicket();
    }
}
