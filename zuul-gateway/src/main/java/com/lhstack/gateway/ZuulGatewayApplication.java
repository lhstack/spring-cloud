package com.lhstack.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author lhstack
 */
@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
@EnableScheduling
@EnableAsync(proxyTargetClass = true)
public class ZuulGatewayApplication {

    public static void main(String[] args) throws Exception {
        initConf();
        SpringApplication.run(ZuulGatewayApplication.class, args);
    }

    private static void initConf() throws Exception {
        String userDir = System.getProperty("user.dir");
        File file = new File(userDir, "conf");
        if (!file.exists()) {
            file.mkdirs();
        }
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(urlClassLoader);
    }

}
