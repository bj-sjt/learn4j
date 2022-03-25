package com.itao.jdk.spi;

import java.util.ServiceLoader;

public class SpiService {
    public static void main(String[] args) {
        ServiceLoader<SpiInterface> services = ServiceLoader.load(SpiInterface.class);
        for (SpiInterface service : services) {
            service.test();
        }
    }
}
