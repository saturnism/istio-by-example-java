## Deploy Application to Kubernetes
Check out the example code:
```
$ cd ~/
$ git clone https://github.com/saturnism/istio-by-example-java.git
$ cd istio-by-example-java/spring-boot-example
```

Deploy the application:
```
$ kubectl apply -f kubernetes/
```

Notice that the YAML files are just regular Kubernetes deployment YAML. This is because Istio 0.2+ can use Kubernetes initializer feature.
During deployment time, Kubernetes will automatically & transparently inject additional sidecars and configurations to these YAML files.

## Addons
### Grafana
Establish port forward from local port 3000 to the Grafana instance:
```
$ kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=grafana -o jsonpath='{.items[0].metadata.name}') 3000:3000
```

Browse to http://localhost:3000 and navigate to Istio Dashboard

### Zipkin
Establish port forward from local port 
```
$ kubectl port-forward -n istio-system $(kubectl get pod -n istio-system -l app=zipkin -o jsonpath='{.items[0].metadata.name}') 9411:9411
```

Browse to http://localhost:9411

### Prometheus
```
$ kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=prometheus -o jsonpath='{.items[0].metadata.name}') 9090:9090
```

### Service Graph
```
$ kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=servicegraph -o jsonpath='{.items[0].metadata.name}') 8088:8088
```

Browse to http://localhost:8088/dotviz
