## Install KEDA and Demo App

Version 2.13.0 is already downloaded here:

```
$ kubectl apply --server-side -f config/keda.yaml
```

Basic demo app from Kubernetes examples:

```
$ kubectl apply -f config/app.yaml
```

You can curl it on port 80. It returns the current time:

```
$ curl localhost:8080/app/
NOW: 2024-01-26 11:36:18.543102411 +0000 UTC m=+6334.579665868
```

# Gateway App

```
$ mvn spring-boot:build-image
$ docker tag gateway:0.0.1-SNAPSHOT localhost:5000/gateway
$ docker push localhost:5000/gateway
$ kubectl apply -f config/gateway.yaml
```

Test that the gateway is working:

```
$ kubectl port-forward services/gateway 8080:80
Forwarding from 127.0.0.1:8080 -> 8080
Forwarding from [::1]:8080 -> 8080
$ curl localhost:8080/app/
NOW: 2024-01-26 11:36:18.543102411 +0000 UTC m=+6334.579665868
```

## Actuator and gRPC Endpoints

Run the app locally (or port forward into the cluster) and try out grpc:

```
$ grpcurl -d '{"name":"app"}' -plaintext localhost:9090 externalscaler.ExternalScaler.IsActive
{
  "result": true
}
```

and use the actuator endpoints to toggle the active flag:

```
$ curl localhost:8080/actuator/scaler?name=foo
true
$ curl -H "Content-Type: application/json" localhost:8080/actuator/scaler -d '{"name": "foo"}'
$ curl localhost:8080/actuator/scaler?name=foo
false
```

and link to grpc `IsActive`:

```
$ grpcurl -d '{"name":"app"}' -plaintext localhost:9090 externalscaler.ExternalScaler.IsActive
{}
$ curl -H "Content-Type: application/json" localhost:8080/actuator/scaler -d '{"name": "foo"}'
$ grpcurl -d '{"name":"app"}' -plaintext localhost:9090 externalscaler.ExternalScaler.IsActive
{
  "result": true
}
```

and `StreamIsActive`:

```
$ grpcurl -d '{"name":"app"}' -plaintext localhost:9090 externalscaler.ExternalScaler.IsActive
{
  "result": true
}
```

blocks until you toggle the active flag and then returns:

```
{}
```

## KEDA scaling

Add the scaled object:

```
$ kubectl apply -f config/scale.yaml
```

and watch the pods:

```
$ watch kubectl get pods
NAME                       READY   STATUS    RESTARTS AGE
app-847f979465-m6n29       1/1     Running   0        8m54s
gateway-776f5fd554-4hp45   1/1     Running   0        14m
```

When you flip the "active" flag the app will scale down to zero, and then scale back up when you toggle back.

## Driving the Scaler

```
$ watch -n 1 kubectl get deployment
NAME      READY   UP-TO-DATE   AVAILABLE   AGE
app       0/0     0            0           7h45m
gateway   1/1     1            1           3h37m
```

Then throw some load on the server:

```
$ ab -c 10 -n 20000 http://localhost:8080/app
```

and watch the deployment scale up:

```
NAME      READY   UP-TO-DATE   AVAILABLE   AGE
app       3/3     3            3           7h46m
gateway   1/1     1            1           3h38m
```

and back to zero when you stop the load.