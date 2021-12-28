package com.lhstack.eureka;

import com.lhstack.eureka.constant.Constant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

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
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) throws Exception {
        initConf();
        initStatefulSet();
        SpringApplication.run(EurekaServerApplication.class, args);
    }

    private static void initStatefulSet() {
        //如果是statefulSet
        if (StringUtils.equalsIgnoreCase(System.getenv(Constant.IS_STATEFUL_SET), Constant.TRUE)) {
            //授权字符串
            String authStr = "";
            if (StringUtils.equalsIgnoreCase(System.getenv(Constant.SECURITY_ENABLE), Constant.TRUE)) {
                String username = System.getenv(Constant.SECURITY_USERNAME);
                String password = System.getenv(Constant.SECURITY_PASSWORD);
                username = StringUtils.isBlank(username) ? Constant.DEFAULT_SECURITY_USERNAME : username;
                password = StringUtils.isBlank(password) ? Constant.DEFAULT_SECURITY_PASSWORD : password;
                authStr = String.format("%s:%s@", username, password);
            }
            //scheme
            String scheme = StringUtils.equalsIgnoreCase(System.getenv(Constant.ENABLE_SSL), Constant.TRUE) ? Constant.HTTPS : Constant.HTTP;
            //k8s中的命名空间
            String namespace = System.getenv(Constant.NAMESPACE);
            namespace = StringUtils.isBlank(namespace) ? Constant.DEFAULT_NAMESPACE : namespace;
            //获得hostname
            String hostname = System.getenv("HOSTNAME");
            //获取到hostname的前缀,如eureka-0 这里获取eureka-
            String hostnamePrefix = StringUtils.substring(hostname, 0, hostname.lastIndexOf("-") + 1);
            int number = Integer.parseInt(StringUtils.substring(hostname, hostname.lastIndexOf("-") + 1));
            String replicasStr = System.getenv(Constant.REPLICAS);
            if (StringUtils.isBlank(replicasStr)) {
                throw new NullPointerException("ENV REPLICAS CANNOT NULL");
            }
            //获取集群数量
            int replicas = Integer.parseInt(replicasStr);
            String serviceName = System.getenv(Constant.SERVICE_NAME);
            if (StringUtils.isBlank(serviceName)) {
                throw new NullPointerException("ENV SERVICE_NAME CANNOT NULL");
            }
            if (replicas == 1) {
                System.setProperty(Constant.EUREKA_REGISTER_URLS_KEY, String.format("http://%slocalhost:8761/eureka", authStr));
            } else {
                StringBuilder eurekaRegisterUrls = new StringBuilder();
                for (int i = 0; i < replicas; i++) {
                    if (i != number) {
                        eurekaRegisterUrls.append(String.format("%s://%s%s%s.%s.%s.svc.cluster.local:8761/eureka,",
                                scheme, authStr, hostnamePrefix, i, serviceName, namespace));
                    }
                }
                System.setProperty(Constant.EUREKA_REGISTER_URLS_KEY, eurekaRegisterUrls.substring(0, eurekaRegisterUrls.length() - 1));
            }
            //规则 http://admin:123456@eureka-0.eureka-headless.eureka.svc.cluster.local:8761/eureka
        }
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
