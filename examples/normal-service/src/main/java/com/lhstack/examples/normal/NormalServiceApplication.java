package com.lhstack.examples.normal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author lhstack
 * @date 2021/12/12
 * @class NormalServiceApplication
 * @since 1.8
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NormalServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NormalServiceApplication.class, args);
    }
}
