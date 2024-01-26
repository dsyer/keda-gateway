An experiment with KEDA. Version 2.13.0 is already downloaded here:

```
$ kubectl apply --server-side -f config/keda.yaml
```

Basic demo app from Kubernetes examples:

```
$ kubectl apply -f config/app.yaml
```

Gateway app:

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

Try out actuator grpc:

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