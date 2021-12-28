package com.lhstack.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author lhstack
 * @date 2021/12/11
 * @class EurekaServerApplication
 * @since 1.8
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAdminServer
public class BootAdminApplication {

    public static void main(String[] args) throws Exception {
        initConf();
        SpringApplication.run(BootAdminApplication.class, args);
    }

    private static void initConf() throws MalformedURLException {
        String userDir = System.getProperty("user.dir");
        File file = new File(userDir, "conf");
        if (!file.exists()) {
            file.mkdirs();
        }
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(urlClassLoader);
    }

}
