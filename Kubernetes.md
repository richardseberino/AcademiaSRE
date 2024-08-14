## conectar no cluster
```
aws eks --region us-east-2 update-kubeconfig --name academia
export KUBECONFIG=/Users/seberino/.kube/config
```
## Criando namespace
```
kubectl create namespace acad
```

## Importando configmap
```
kubectl create configmap kafka-export-conf --from-file=Gm4cArtefatos/kubernetes/configs/kafkaExporter/application.conf -n acad
```

## Criacao Kafka
```
kubectl create -f Gm4cArtefatos/kubernetes/yaml/kafka.yaml
kubectl create -f Gm4cArtefatos/kubernetes/yaml/kafka-svc.yaml
sleep 120
export kafka_pod=`kubectl get pod -n acad | grep kafka | grep -v NAME | grep -v exporter | awk '{ print $1 } '`
kubectl exec -ti $kafka_pod -n acad -- kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic tef
kubectl exec -ti $kafka_pod -n acad -- kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic senha
kubectl exec -ti $kafka_pod -n acad -- kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic conta
kubectl exec -ti $kafka_pod -n acad -- kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic limite
```

## Criacao Cassandra
```
kubectl create -f Gm4cArtefatos/kubernetes/yaml/cassandra.yaml
kubectl create -f Gm4cArtefatos/kubernetes/yaml/cassandra-svc.yaml
export cassandra_pod=`kubectl get pod -n acad | grep cassandra | grep -v NAME | awk '{ print $1 } '`
sleep 90
kubectl cp Gm4cArtefatos/openshift/cassandra.sql $cassandra_pod:/tmp -n acad
kubectl exec -ti $cassandra_pod -n acad -- cqlsh -u cassandra -p cassandra -f /tmp/cassandra.sql
```

## Criacao Jaeger
```
kubectl create -f Gm4cArtefatos/kubernetes/yaml/jaeger.yaml
kubectl create -f Gm4cArtefatos/kubernetes/yaml/jaeger-svc.yaml
```

## Prometheus
```
kubectl create configmap prometheus-yml --from-file=Gm4cArtefatos/Prometheus/prometheus.yml -n acad
kubectl create -f Gm4cArtefatos/kubernetes/yaml/prometheus.yaml
kubectl create -f Gm4cArtefatos/kubernetes/yaml/prometheus-svc.yaml
```

## Grafana
```
kubectl create -f Gm4cArtefatos/kubernetes/yaml/grafana.yaml
kubectl create -f Gm4cArtefatos/kubernetes/yaml/grafana-svc.yaml
```

## Log Pipeline
```
kubectl create -f https://download.elastic.co/downloads/eck/2.3.0/crds.yaml
kubectl apply -f https://download.elastic.co/downloads/eck/2.3.0/operator.yaml
kubectl create -f Gm4cArtefatos/kubernetes/yaml/log-pipeline.yaml
kubectl create -f Gm4cArtefatos/kubernetes/yaml/filebeat-kubernetes.yaml
#curl -L -O https://raw.githubusercontent.com/elastic/beats/8.3/deploy/kubernetes/filebeat-kubernetes.yaml


cd Gm4cArtefatos/kubernetes/elk/filebeat/
helm install filebeat . 
cd ../logstash
helm install logstash . 
cd ../elastic
helm install elasticsearch .
cd ../kibana
helm install kibana . 
```
### Recupera Login do Kibana
```
kubectl get secret es-es-elastic-user -o=jsonpath='{.data.elastic}' -n acad | base64 --decode; echo
kubectl expose deploy kibana-kb --name kibana-kb-http --port 5601 -n acad
```

## Prepara Redis
```
kubectl create -f Gm4cArtefatos/kubernetes/yaml/redis.yaml
```

## Prepara aplicações
```
kubectl create configmap conta-application -n acad --from-file=Gm4cConta/src/main/resources/application.yml
kubectl create configmap senha-application -n acad --from-file=Gm4cSenha/src/main/resources/application.yml
kubectl create configmap tef-application -n acad --from-file=Gm4cTEF/src/main/resources/application.yml
kubectl create configmap limite-application -n acad --from-file=Gm4cLimite/src/main/resources/application.yml
source setup_image_completo
```
### substitua no comando abaixo seu id docker-Hub e o nome da sua imagem 
```
docker build . -t seberino/gm4c:2.1
docker push seberino/gm4c:2.1
```
### edite o arquivo Gm4cArtefatos/kubernetes/gm4c.yaml e substitua o nome do seu id Docker Hub e da imagem definida no passo anterior
### aplique o arquivo abaixo
```
kubectl create -f Gm4cArtefatos/kubernetes/yaml/gm4c.yaml
```

## Instala o Kuberneteds Dashboard
```
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.5.0/aio/deploy/recommended.yaml
``` 
