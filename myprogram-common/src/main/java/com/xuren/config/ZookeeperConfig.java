package com.xuren.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {"classpath:zookeeper.properties"},
        ignoreResourceNotFound = false, encoding = "UTF-8", name = "zookeeper.properties")
public class ZookeeperConfig {
    @Value("${zookeeper.address}")
    private String address;

    public String getAddress() {
        return address;
    }
}
