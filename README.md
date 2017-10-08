## Setup a Kubernetes cluster on Google Cloud Platform

You'll need a [Google Cloud Platform account](https://cloud.google.com/), a [Project](https://cloud.google.com/resource-manager/docs/creating-managing-projects), and [gcloud SDK](https://cloud.google.com/sdk/).

First, create a new Kubernetes cluster with alpha features to take advantage of Kubernetes initializer.
```
$ export ISTIO_PROJECT_ID=your_project_id
$ gcloud --project=$ISTIO_PROJECT_ID alpha container clusters create istio-cluster --zone=us-central1-c --num-nodes=4 --machine-type=n1-standard-4 --enable-kubernetes-alpha
```

Once the cluster was created, make sure your user account has admin role within the Kubernetes cluster:
```
$ kubectl create clusterrolebinding cluster-admin-binding --clusterrole=cluster-admin --user=$(gcloud config get-value core/account)
```

## Install Istio

Install Istio CLI:
```
$ cd ~/
$ curl -L https://git.io/getLatestIstio | sh -
$ export PATH="$PATH:$HOME/istio-0.2.7/bin"
```

Install Istio Service Mesh in Kubernetes:
```
$ cd ~/istio-0.2.7
$ kubectl apply -f install/kubernetes/istio.yaml
$ kubectl apply -f install/kubernetes/istio-initializer.yaml
```

Install Add-ons for Grafana, Prometheus, and Zipkin:
```
$ cd ~/istio-0.2.7
$ kubectl apply -f install/kubernetes/addons/zipkin.yaml
$ kubectl apply -f install/kubernetes/addons/grafana.yaml
$ kubectl apply -f install/kubernetes/addons/prometheus.yaml
$ kubectl apply -f install/kubernetes/addons/servicegraph.yaml
```

Check the status and make sure all the components are in running state before continuing:
```
$ kubectl get pods -n istio-system
```

Enable firewall to allow connection to the Istio ingress:
```
$ gcloud --project=$ISTIO_PROJECT_ID compute firewall-rules create allow-istio-ingress \ 
  --allow tcp:$(kubectl get svc istio-ingress -n istio-system -o jsonpath='{.spec.ports[0].nodePort}')
```

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
