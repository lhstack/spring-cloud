## 构建eureka镜像

```dockerfile
FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.13_8
MAINTAINER <lhstack@foxmail.com>
ENV JAVA_OPTS="-Xmx128m -Xms128m"
EXPOSE 7000
RUN mkdir -p /config-server/conf
ADD *.jar /config-server/app.jar
COPY conf /config-server/conf
WORKDIR /config-server
CMD java $JAVA_OPTS -Dserver.port=7000 -jar app.jar

```

## 目录结构如下

![img.png](images/1.png)

`其中conf目录可以看作为classpath目录,可以放classpath的一些资源，config子目录，则为config-server本地配置目录`

```shell
# 构建镜像
docker build -t config-server:11-alpine .
# 导出镜像
docker save -o config.tar config-server:11-alpine
# 导入镜像
docker import config.tar
```
## config-server.yaml
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: config-server
data:
  EUREKA_SERVER_URL: "http://admin:123456@10.43.87.61:8761/eureka"
  SECURITY_USERNAME: "admin"
  SECURITY_PASSWORD: "123456"
  SECURITY_ENABLE: "true"
  JAVA_OPTS: "-Xmx512m -Xms512m"
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: config-server-localconfig
data:
  zuul-gateway.yaml: |
    zuul:
      ignored-headers:
      sensitive-headers:
      add-host-header: true
    hystrix:
      command:
        default:
          execution:
            isolation:
              thread:
                timeoutInMilliseconds: 60000
    ribbon:
      ReadTimeout: 2000
      ConnectTimeout: 2000
      MaxAutoRetries: 3
      MaxAutoRetriesNextServer: 2
  application.yaml: |
    management:
      endpoints:
        web:
          exposure:
            include: ['health','info']
---
apiVersion: v1
kind: Service
metadata:
  name: config-server
spec:
  selector:
    app: config-server
    register-service: eureka
  ports:
    - port: 7000
  type: ClusterIP
---
apiVersion: v1
kind: ReplicationController
metadata:
  name: config-server
spec:
  replicas: 2
  selector:
    app: config-server
    register-service: eureka
  template:
    metadata:
      labels:
        app: config-server
        register-service: eureka
    spec:
      volumes:
        - name: config
          configMap:
            name: config-server-localconfig
      containers:
        - name: config-server
          image: config-server:11-alpine
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 7000
          envFrom:
            - configMapRef:
                name: config-server
          volumeMounts:
            - name: config
              mountPath: /config-server/conf/config
          resources:
            requests:
              cpu: 50m
              memory: 64Mi
            limits:
              cpu: 100m
              memory: 512Mi
          readinessProbe:
            httpGet:
              path: /actuator/info
              port: 7000
            initialDelaySeconds: 5
            failureThreshold: 5
            periodSeconds: 5
            successThreshold: 1
            timeoutSeconds: 2
          livenessProbe:
            httpGet:
              path: /actuator/info
              port: 7000
            initialDelaySeconds: 60
            failureThreshold: 5
            periodSeconds: 120
            successThreshold: 1
            timeoutSeconds: 2

```
## 安装集群
```shell
kubectl apply -f config-server.yaml
```
## 成功界面如下
![img.png](images/2.png)