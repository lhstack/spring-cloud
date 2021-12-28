package com.lhstack.gateway.listener;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lhstack
 * @date 2021/12/12
 * @class DynamicRouteConfiguration
 * @since 1.8
 */
@Component
public class DynamicRouteListener implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicRouteListener.class);

    @Autowired
    private SimpleRouteLocator simpleRouteLocator;

    @Autowired
    private ZuulProperties zuulProperties;

    @Autowired
    private ApplicationEventPublisher publisher;

    private final AtomicLong fileLength = new AtomicLong(0);


    @Scheduled(fixedDelay = 1000,initialDelay = 60000)
    @Async
    public void scheduler() throws IOException {
        File routerFile = getRouterFile();
        if(fileLength.get() != routerFile.length()){
            LOGGER.info("check route change event,refresh router");
            refresh();
            fileLength.set(routerFile.length());
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.refresh();
    }

    public void refresh() throws IOException {
        File routerFile = getRouterFile();
        if(fileLength.get() == 0){
            fileLength.set(routerFile.length());
        }
        FileInputStream fi = new FileInputStream(routerFile);
        Map<String, ZuulProperties.ZuulRoute> routeMap = JSONObject.parseObject(new String(fi.readAllBytes(), StandardCharsets.UTF_8), new TypeReference<>() {
        });
        zuulProperties.getRoutes().putAll(routeMap);
        simpleRouteLocator.getRoutes().clear();
        for (Map.Entry<String, ZuulProperties.ZuulRoute> entry : routeMap.entrySet()) {
            entry.getValue().setId(entry.getKey());
            simpleRouteLocator.getRoutes().add(entry.getValue().getRoute(zuulProperties.getPrefix()));
        }
        fi.close();
        publisher.publishEvent(new RoutesRefreshedEvent(this.simpleRouteLocator));
    }

    public File getRouterFile() throws IOException {
        String routeDirector = System.getProperty("user.dir") + "/router/router.json";
        File file = new File(routeDirector);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
}
