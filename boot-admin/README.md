## 构建eureka镜像

```dockerfile
FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.13_8
MAINTAINER <lhstack@foxmail.com>
ENV JAVA_OPTS="-Xmx128m -Xms128m"
EXPOSE 8800
RUN mkdir -p /admin/conf
ADD *.jar /admin/app.jar
COPY conf /admin/conf
WORKDIR /admin
CMD java $JAVA_OPTS -Dserver.port=8800 -jar app.jar
```

## 目录结构如下,同eureka目录一样

![1.png](images/1.png)

`其中conf目录可以看作为classpath目录,可以放classpath的一些资源`

```shell
# 构建镜像
docker build -t boot-admin:11-alpine .
# 导出镜像
docker save -o admin.tar boot-admin:11-alpine
# 导入镜像
docker import admin.tar
```
## admin.yaml
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: boot-admin
data:
  EUREKA_SERVER_URL: "http://admin:123456@10.43.87.61:8761/eureka"
  SECURITY_USERNAME: "admin"
  SECURITY_PASSWORD: "123456"
  JAVA_OPTS: "-Xmx512m -Xms512m"
---
apiVersion: v1
kind: Service
metadata:
  name: boot-admin
spec:
  selector:
    app: boot-admin
    register-service: eureka
  ports:
    - port: 8800
  type: ClusterIP
---
apiVersion: v1
kind: ReplicationController
metadata:
  name: boot-admin
spec:
  replicas: 1
  selector:
    app: boot-admin
    register-service: eureka
  template:
    metadata:
      labels:
        app: boot-admin
        register-service: eureka
    spec:
      containers:
        - name: boot-admin
          image: boot-admin:11-alpine
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8800
          envFrom:
            - configMapRef:
                name: boot-admin
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
              port: 8800
            initialDelaySeconds: 5
            failureThreshold: 5
            periodSeconds: 5
            successThreshold: 1
            timeoutSeconds: 2
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 8800
            initialDelaySeconds: 60
            failureThreshold: 5
            periodSeconds: 120
            successThreshold: 1
            timeoutSeconds: 2
```
## 安装集群
```shell
kubectl apply -f admin.yaml
```
## 成功界面如下
![2.png](images/2.png)