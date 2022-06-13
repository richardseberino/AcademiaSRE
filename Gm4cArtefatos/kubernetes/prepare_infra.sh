echo "fazendo deploy do Kafka"
kubectl apply -f yaml/kafka.yaml
echo "expondo o kafka como serviço"
kubectl apply -f yaml/kafka-svc.yaml
echo "fazendo deploy do cassandra"
kubectl apply -f yaml/cassandra.yaml
echo "expondo o cassandra para a aplicacao"
kubectl apply -f yaml/cassandra-svc.yaml
echo "fazendo deploy do jaeger"
kubectl apply -f yaml/jaeger.yaml
echo "expondo jaeger"
kubectl apply -f yaml/jaeger-svc.yaml
echo "fazendo deploy do prometheus"
kubectl apply -f yaml/prometheus.yaml
echo "expondo prometheus"
kubectl apply -f yaml/prometheus-svc.yaml
echo "fazendo deploy do grafana"
kubectl apply -f yaml/grafana.yaml
echo "expondo grafana"
kubectl apply -f yaml/grafana-svc.yaml

echo "aguardando serviço do kafka subir para criar objetos"
sleep 10
echo "criando os topicos da aplicacao de referencia"
kubectl get pods | grep kafka | awk '{ print "kubectl exec -ti " $1 " -- sh -c /tmp/scripts/init_kafka.sh"} '| bash
echo "criando e populando a base de dados no cassandra"
kubectl get pods | grep cassandra | awk '{ print "kubectl exec -ti " $1 " -- sh -c /tmp/scripts/init_cassandra.sh"} '| bash


kubectl create configmap conta-application --from-file=/root/gm4c/Gm4cConta/src/main/resources/application.yml
kubectl create configmap senha-application --from-file=/root/gm4c/Gm4cSenha/src/main/resources/application.yml
kubectl create configmap tef-application --from-file=/root/gm4c/Gm4cTEF/src/main/resources/application.yml
kubectl create configmap limite-application --from-file=/root/gm4c/Gm4cLimite/src/main/resources/application.yml
kubectl create configmap kibana-yml --from-file=/root/gm4c/Gm4cArtefatos/kubernetes/configs/kibana/kibana.yml
kubectl create configmap prometheus-yml --from-file=/root/gm4c/Gm4cArtefatos/Prometheus/prometheus.yml
kubectl create configmap logstash-yml --from-file=/root/gm4c/Gm4cArtefatos/kubernetes/configs/logstash/logstash.yml
kubectl create configmap logstash-conf --from-file=/root/gm4c/Gm4cArtefatos/kubernetes/configs/logstash/logstash.conf
kubectl create configmap kafka-export-conf --from-file=/root/gm4c/Gm4cArtefatos/kubernetes/configs/kafkaExporter/application.conf



kubectl apply -f yaml/log-pipeline.yaml

kubectl apply -f yaml/ldap.yaml
sleep 20
. scripts/setupLdap.sh

mkdir /root/volumes/jenkins-data
chown 1000:1000 /root/volumes/jenkins-data
kubectl apply -f yaml/jenkins.yaml
kubectl apply -f yaml/redis.yaml

kubectl apply -f yaml/gm4c.yaml
