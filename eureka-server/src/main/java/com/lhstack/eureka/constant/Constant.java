package com.lhstack.eureka.constant;

/**
 * @author lhstack
 * @date 2021/12/11
 * @class Constant
 * @since 1.8
 */
public interface Constant {
    /**
     * 是否是k8s的statefulSet
     */
    String IS_STATEFUL_SET = "IS_STATEFUL_SET";

    /**
     * 是否开启安全认证
     */
    String SECURITY_ENABLE = "SECURITY_ENABLE";

    /**
     * 用户名
     */
    String SECURITY_USERNAME = "SECURITY_USERNAME";

    /**
     * 密码
     */
    String SECURITY_PASSWORD = "SECURITY_PASSWORD";

    /**
     * 无头服务名称
     */
    String SERVICE_NAME = "SERVICE_NAME";

    /**
     * eureka 服务注册key
     */
    String EUREKA_REGISTER_URLS_KEY = "eureka.client.service-url.defaultZone";

    /**
     * 集群数量
     */
    String REPLICAS = "REPLICAS";

    /**
     * 是否开启ssl
     */
    String ENABLE_SSL = "ENABLE_SSL";

    /**
     * 命名空间
     */
    String NAMESPACE = "NAMESPACE";

    /**
     * 默认命名空间
     */
    String DEFAULT_NAMESPACE = "default";

    /**
     * 默认用户名
     */
    String DEFAULT_SECURITY_USERNAME = "admin";

    /**
     * 默认密码
     */
    String DEFAULT_SECURITY_PASSWORD = "123456";

    String TRUE = "true";

    String FALSE = "false";

    String HTTP = "http";

    String HTTPS = "https";

}
