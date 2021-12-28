package com.lhstack.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author lhstack
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) throws Exception {
        initConf();
        SpringApplication.run(ConfigServerApplication.class, args);
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
