## Setup a Kubernetes cluster on Google Cloud Platform

You'll need a [Google Cloud Platform account](https://cloud.google.com/), a [Project](https://cloud.google.com/resource-manager/docs/creating-managing-projects), and [gcloud SDK](https://cloud.google.com/sdk/).

First, create a new Kubernetes cluster to take advantage of Kubernetes webook to inject initializer:
```
$ export ISTIO_PROJECT_ID=$(gcloud config get-value core/project)
$ gcloud --project=$ISTIO_PROJECT_ID alpha container clusters create istio-cluster \
  --zone=us-central1-c --num-nodes=4 --machine-type=n1-standard-4 \
  --cluster-version=1.9.2-gke.1
```

Once the cluster was created, make sure your user account has admin role within the Kubernetes cluster:
```
$ kubectl create clusterrolebinding cluster-admin-binding --clusterrole=cluster-admin --user=$(gcloud config get-value core/account)
```

## Install Istio

Install Istio CLI. Download [Istio 0.5.1 release](https://github.com/istio/istio/releases/tag/0.5.1).
Unpack the package and add it to your PATH, e.g.:

```
$ tar xzvf istio-0.5.1_osx.tar.gz
$ export PATH="$PATH:$HOME/istio-0.5.1/bin"
```

Install Istio Service Mesh in Kubernetes, without Auth. (Auth causes trouble with health check and readiness check at the moment).
```
$ cd ~/istio-0.5.1
$ kubectl apply -f install/kubernetes/istio.yaml
```

Install Add-ons for Grafana, Prometheus, and Zipkin:
```
$ cd ~/istio-0.2.7
$ kubectl apply -f install/kubernetes/addons/zipkin.yaml
$ kubectl apply -f install/kubernetes/addons/grafana.yaml
$ kubectl apply -f install/kubernetes/addons/prometheus.yaml
$ kubectl apply -f install/kubernetes/addons/servicegraph.yaml
```

Install Istio Injector Webhook - follow the [Istio injector installation guide](https://istio.io/docs/setup/kubernetes/sidecar-injection.html#automatic-sidecar-injection).
Also, don't forget to mark default namespace as injection enabled:
```
$ kubectl label namespace default istio-injection=enabled
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
