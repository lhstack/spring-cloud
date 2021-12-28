## 构建eureka镜像

```dockerfile
FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.13_8
MAINTAINER <lhstack@foxmail.com>
ENV JAVA_OPTS="-Xmx128m -Xms128m"
EXPOSE 9999
RUN mkdir -p /gateway/conf
ADD *.jar /gateway/app.jar
COPY conf /gateway/conf
COPY router /gateway/router
WORKDIR /gateway
CMD java $JAVA_OPTS -Dserver.port=9999 -jar app.jar
```

## 目录结构如下,同eureka目录一样

![1.png](images/1.png)

`其中conf目录可以看作为classpath目录,可以放classpath的一些资源`
`router目录存放路由配置，修改配置即更新路由信息`

```shell
# 构建镜像
docker build -t zuul-gateway:11-alpine .
# 导出镜像
docker save -o zuul.tar zuul-gateway:11-alpine
# 导入镜像
docker import zuul.tar
```
## zuul.yaml
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: zuul-gateway
data:
  EUREKA_SERVER_URL: "http://admin:123456@10.43.87.61:8761/eureka"
  SECURITY_USERNAME: "admin"
  SECURITY_PASSWORD: "123456"
  JAVA_OPTS: "-Xmx512m -Xms512m"
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: zuul-gateway-router
data:
  router.json: |
    {
      "baidu": {
         "path": "/baidu/**",
         "stripPrefix": true,
         "url": "https://www.baidu.com"
       }
    }
---
apiVersion: v1
kind: Service
metadata:
  name: zuul-gateway
spec:
  selector:
    app: zuul-gateway
    register-service: eureka
  ports:
    - port: 9999
  type: ClusterIP
---
apiVersion: v1
kind: ReplicationController
metadata:
  name: zuul-gateway
spec:
  replicas: 2
  selector:
    app: zuul-gateway
    register-service: eureka
  template:
    metadata:
      labels:
        app: zuul-gateway
        register-service: eureka
    spec:
      volumes:
        - name: router
          configMap:
            name: zuul-gateway-router
            items:
              - key: router.json
                path: router.json
      containers:
        - name: zuul-gateway
          image: zuul-gateway:11-alpine
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 9999
          envFrom:
            - configMapRef:
                name: zuul-gateway
          volumeMounts:
            - mountPath: /gateway/router/router.json
              name: router
              subPath: router.json
          resources:
            requests:
              cpu: 50m
              memory: 64Mi
            limits:
              cpu: 100m
              memory: 512Mi
          readinessProbe:
            httpGet:
              path: /actuator/health
              port: 9999
            initialDelaySeconds: 5
            failureThreshold: 5
            periodSeconds: 5
            successThreshold: 1
            timeoutSeconds: 2
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 9999
            initialDelaySeconds: 60
            failureThreshold: 5
            periodSeconds: 120
            successThreshold: 1
            timeoutSeconds: 2
```
## 安装集群
```shell
kubectl apply -f zuul.yaml
```
## 成功界面如下
![2.png](images/2.png)
![3.png](images/3.png)
![4.png](images/4.png)