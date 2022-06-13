# Mini-TEF Project
## 1.0 Introdução
A idea deste repositorio é compartilhar uma aplicação baseada em microserviços e Saga Patern com conceitos de SRE (Observabilidade, intrumentação de código, etc)

## 2.0 Preparação do Ambiente (infraestrutura)
Para este projeto funcionar vamos precisar de algumas peças de infraestrutura (Kafka, Jaeger, ElasticSearch, Kibana, Prometheus, Grafana e Cassandra) usados na solução

### 2.1 Preparação
```
oc create namespace tef
oc project tef
oc create -f Gm4cArtefatos/openshift/service-account.yaml -n tef
oc adm policy add-scc-to-user privileged -z tef -n tef
```

### 2.2 Deploy do Kafka
```
oc project tef
oc create -f Gm4cArtefatos/openshift/kafka.yaml -n tef
oc create -f Gm4cArtefatos/openshift/kafka-svc.yaml -n tef
```
Aguarde o pod do Kafka ser criado 
```
oc get pods -n tef
```

Depois do pod do Kafka esta no ar rode os comandos abaixo para criar os tópicos
```
oc exec -ti kafka-0 -n tef -- kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic tef
oc exec -ti kafka-0 -n tef -- kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic senha
oc exec -ti kafka-0 -n tef -- kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic conta
oc exec -ti kafka-0 -n tef -- kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic limite
```

Execute os comandos abaixo para subir o Exporter do Kafa para o prometheus
```
oc create configmap kafka-export-conf -n tef --from-file=Gm4cArtefatos/kubernetes/configs/kafkaExporter/application.conf
oc create -f Gm4cArtefatos/openshift/kafka-exporter.yaml -n tef

```

### 2.3 Deploy do Cassandra
```
oc create -f Gm4cArtefatos/openshift/cassandra.yaml -n tef
oc create -f Gm4cArtefatos/openshift/cassandra-svc.yaml -n tef
```
Aguarde que o pod do Cassandra seja criado
```
oc get pods -n tef
```
Copie o script para dentro do pod do Cassandra e execute ele
```
oc cp Gm4cArtefatos/openshift/cassandra.sql cassandra-0:/tmp -n tef
oc exec -ti cassandra-0 -n tef -- cqlsh -u cassandra -p cassandra -f /tmp/cassandra.sql
```

### 2.4 Deploy Jaeger
```
oc create -f Gm4cArtefatos/openshift/jaeger.yaml
oc create -f Gm4cArtefatos/openshift/jaeger-svc.yaml
```


### 2.5 Deploy do Prometheus + Grafana
```
oc create configmap prometheus-yml -n tef --from-file=Gm4cArtefatos/Prometheus/prometheus.yml
oc create configmap prometheus-yml -n tef --from-file=Gm4cArtefatos/Prometheus/prometheus.yml
oc create -f Gm4cArtefatos/openshift/prometheus.yaml -n tef
oc create -f Gm4cArtefatos/openshift/prometheus-svc.yaml -n tef
oc create -f Gm4cArtefatos/openshift/prometheus-route.yaml -n tef
oc create -f Gm4cArtefatos/openshift/grafana.yaml -n tef
oc create -f Gm4cArtefatos/openshift/grafana-svc.yaml -n tef
oc create -f Gm4cArtefatos/openshift/grafana-route.yaml -n tef

```

### 2.6 Deploy do Elastic Search + Kibana
```
oc create -f Gm4cArtefatos/openshift/Log-pipeline.yaml
```
Aguarde o Operator ser instalado
```
oc get csv -n openshift-logging
```
Crie uma instancia de ElasticSearch + Kibana
Nota: Se você não estiver usando a IBM Cloud, você vai precisar ajustar o nome do Storage Class no arquivo Gm4cArtefatos/openshift/es-kibana.yaml (linha 20)
```
oc create -f Gm4cArtefatos/openshift/es-kibana.yaml
```
Configure o Index do Kibana para aplicações, para isso vamos acessar a interface do Kibana, execute o comando abaixo para descobrir a URL do Kibana:
```
oc get route -n openshift-logging | grep kibana
```
Use a URL recuperada no passo anterior e cole ela no seu navegador, na primeira tela (depois de logado) você vai precisar configurar o index principal das aplicações
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/images/kibana01.png)
informe o texto "app*" e clique em "next step"

![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/images/kibana02.png)
Selecione o campo "@timestamp" como fonte de dados para a data/horário dos logs e clique em "Create index pattern"

### 2.7 Deploy Nexus
```
oc create -f Gm4cArtefatos/openshift/nexus.yaml -n tef
oc new-app nexus3-persistent
```


## 3.0 Instalar as aplicações
### 3.1 Preparação
```
oc create configmap conta-application -n tef --from-file=Gm4cConta/src/main/resources/application.yml
oc create configmap senha-application -n tef --from-file=Gm4cSenha/src/main/resources/application.yml
oc create configmap tef-application -n tef --from-file=Gm4cTEF/src/main/resources/application.yml
oc create configmap limite-application -n tef --from-file=Gm4cLimite/src/main/resources/application.yml
```

