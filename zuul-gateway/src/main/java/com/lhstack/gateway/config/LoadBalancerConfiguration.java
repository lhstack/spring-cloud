package com.lhstack.gateway.config;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author lhstack
 * @date 2021/12/12
 * @class LoadBalancerConfiguration
 * @since 1.8
 */
@RibbonClients(defaultConfiguration = LoadBalancerConfiguration.class)
public class LoadBalancerConfiguration {

    public static class GrayRule extends AbstractLoadBalancerRule {

        private final Random random = new Random();

        @Override
        public void initWithNiwsConfig(IClientConfig iClientConfig) {

        }

        @Override
        public Server choose(Object key) {
            HttpServletRequest request = (HttpServletRequest) key;
            String version = request.getHeader("version");
            List<Server> allServers = getLoadBalancer().getAllServers();
            if (CollectionUtils.isEmpty(allServers)) {
                return null;
            }
            if (StringUtils.isNotBlank(version)) {
                List<Server> grayServerList = allServers.stream().filter(item -> {
                    DiscoveryEnabledServer enabledServer = (DiscoveryEnabledServer) item;
                    Map<String, String> metadata = enabledServer.getInstanceInfo().getMetadata();
                    return metadata.containsKey("version") && StringUtils.equals(metadata.get("version"), version);
                }).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(grayServerList)) {
                    return grayServerList.get(random.nextInt(grayServerList.size()));
                }
            }
            return allServers.get(random.nextInt(allServers.size()));
        }
    }

    @Bean
    public IRule grayRule() {
        return new GrayRule();
    }
}
