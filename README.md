# Mini-TEF Project
## 1.0 Introdução
A idea deste repositorio é compartilhar uma aplicação baseada em microserviços e Saga Patern com conceitos de SRE (Observabilidade, intrumentação de código, etc)
A aplicação em questão é uma réplica simplificada da TEF, como menos componentes, porém do ponto de arquitetura muito similar, usando Cassandra, Kafka, Redis, e aplicações escritas em SpringBoot/Java.
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/architecture02.png)


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
oc exec -ti kafka-0 -n tef -- kafka-topics --create --bootstrap-server localhost:9092  --replication-factor 1 --partitions 3 --topic tef
oc exec -ti kafka-0 -n tef -- kafka-topics --create --bootstrap-server localhost:9092  --replication-factor 1 --partitions 3 --topic senha
oc exec -ti kafka-0 -n tef -- kafka-topics --create --bootstrap-server localhost:9092  --replication-factor 1 --partitions 3 --topic conta
oc exec -ti kafka-0 -n tef -- kafka-topics --create --bootstrap-server localhost:9092  --replication-factor 1 --partitions 3 --topic  limite
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
oc create -f Gm4cArtefatos/openshift/prom.yaml -n tef
oc create -f Gm4cArtefatos/openshift/grafana.yaml -n tef
oc create -f Gm4cArtefatos/openshift/grafana-svc.yaml -n tef
oc create -f Gm4cArtefatos/openshift/grafana-route.yaml -n tef
oc adm policy add-cluster-role-to-user cluster-monitoring-view -z tef -n tef
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
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/kibana01.png)
informe o texto "app*" e clique em "next step"

![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/kibana02.png)
Selecione o campo "@timestamp" como fonte de dados para a data/horário dos logs e clique em "Create index pattern"

### 2.7 Deploy Nexus
```
oc create -f Gm4cArtefatos/openshift/nexus.yaml -n tef
oc new-app nexus3-persistent
```

### 2.8 Deploy Redis
```
oc create -f Gm4cArtefatos/openshift/redis.yaml -n tef
```

## 3.0 Instalar as aplicações
### 3.1 Preparação (Compilação das Dependencias)
```
export MVN_URL=http://nexus-tef.mycluster-sao01-b3-321291-510ad6ebead8e7457a6e62904edfa48f-0000.us-south.containers.appdomain.cloud/repository/maven-snapshots/
cd Gm4cLogging
mvn install
mvn package
mvn deploy:deploy-file -DgroupId=com.gm4c -DartifactId=Gm4cLogging -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -Dfile=target/Gm4cLogging-0.0.1-SNAPSHOT.jar -DgeneratePom=true -DrepositoryId=nexus -Durl=$MVN_URL
cd ..
cd Gm4cLog4J2
mvn install
mvn package
mvn deploy:deploy-file -DgroupId=com.gm4c -DartifactId=Gm4cLog4J2 -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -Dfile=target/Gm4cLog4J2-0.0.1-SNAPSHOT.jar -DgeneratePom=true -DrepositoryId=nexus -Durl=$MVN_URL
cd ..
cd Gm4cTrace
mvn install
mvn package
mvn deploy:deploy-file -DgroupId=com.gm4c -DartifactId=Gm4cTrace -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -Dfile=target/Gm4cTrace-0.0.1-SNAPSHOT.jar -DgeneratePom=true -DrepositoryId=nexus -Durl=$MVN_URL
cd ..
cd Gm4cHealthCheck
mvn install
mvn package
mvn deploy:deploy-file -DgroupId=com.gm4c -DartifactId=Gm4cHealthCheck -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -Dfile=target/Gm4cHealthCheck-0.0.1-SNAPSHOT.jar -DgeneratePom=true -DrepositoryId=nexus -Durl=$MVN_URL
cd ..
cd Gm4cCommons
mvn install
mvn package
mvn deploy:deploy-file -DgroupId=com.gm4c -DartifactId=Gm4cCommons -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -Dfile=target/Gm4cCommons-0.0.1-SNAPSHOT.jar -DgeneratePom=true -DrepositoryId=nexus -Durl=$MVN_URL
cd ..
cd Gm4cArtefatos
mvn deploy:deploy-file -DgroupId=io.confluent -DartifactId=kafka-avro-serializer -Dversion=4.0.0 -Dpackaging=jar -Dfile=kafka-avro-serializer-4.0.0.jar -DgeneratePom=true -DrepositoryId=nexus -Durl=$MVN_URL
```

### 3.2 Deploy Conta
Abre a perspectiva de "Developer" na console do OpenShift e clique em "Add" como mostra na imagem abaixo e clique em "Import from Git"
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/oc-dev01.png)

Informe o caminho para o repositório git "https://github.com/richardseberino/AcademiaSRE.git", informe o contexto para a aplicação Conta "/Gm4cConta" e escolha o tipo de deploy "Builder Image" como na imagem abaixo:
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/conta01.png)

Selecione / informe o nome da aplicação como "tef-app", o nome do nosso componente como "conta", garanta que a imagem de build selecionada é "java" e o jdk "Red Hat Open JDK 17 (UBI 8) e clique em "Create" como na imagem abaixo:
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/conta02.png)


### 3.3 Deploy Limite
Abre a perspectiva de "Developer" na console do OpenShift e clique em "Add" como mostra na imagem abaixo e clique em "Import from Git"
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/oc-dev01.png)

Informe o caminho para o repositório git "https://github.com/richardseberino/AcademiaSRE.git", informe o contexto para a aplicação Limite "/Gm4cLimite" e escolha o tipo de deploy "Builder Image" como na imagem abaixo:
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/limite01.png)

Selecione / informe o nome da aplicação como "tef-app", o nome do nosso componente como "limite", garanta que a imagem de build selecionada é "java" e o jdk "Red Hat Open JDK 17 (UBI 8) e clique em "Create" como na imagem abaixo:
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/limite02.png)

### 3.4 Deploy Senha
Abre a perspectiva de "Developer" na console do OpenShift e clique em "Add" como mostra na imagem abaixo e clique em "Import from Git"
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/oc-dev01.png)

Informe o caminho para o repositório git "https://github.com/richardseberino/AcademiaSRE.git", informe o contexto para a aplicação Senha "/Gm4cSenha" e escolha o tipo de deploy "Builder Image" como na imagem abaixo:
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/senha01.png)

Selecione / informe o nome da aplicação como "tef-app", o nome do nosso componente como "senha", garanta que a imagem de build selecionada é "java" e o jdk "Red Hat Open JDK 17 (UBI 8) e clique em "Create" como na imagem abaixo:
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/senha02.png)

### 3.5 Deploy TEF
Abre a perspectiva de "Developer" na console do OpenShift e clique em "Add" como mostra na imagem abaixo e clique em "Import from Git"
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/oc-dev01.png)

Informe o caminho para o repositório git "https://github.com/richardseberino/AcademiaSRE.git", informe o contexto para a aplicação Tef "/Gm4cTEF" e escolha o tipo de deploy "Builder Image" como na imagem abaixo:
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/tef01.png)

Selecione / informe o nome da aplicação como "tef-app", o nome do nosso componente como "tef", garanta que a imagem de build selecionada é "java" e o jdk "Red Hat Open JDK 17 (UBI 8) e clique em "Create" como na imagem abaixo:
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/tef02.png)

### 3.6 Deploy SpringBoot Admin 
Execute o comando abaixo para instalar o SpringBoot Admin para monitorar a saúde dos componentes
```
oc create -f Gm4cArtefatos/openshift/springboot-admin.yaml
```
Execute o comando abaixo para recuperar a rota da interface gráfica do SpringBoot Admin
```
oc get route -n tef | grep springboot
```

## 4.0 Configura monitoração

### 4.1 Grafana

#### 4.1.1 Conectando o Prometheus ao Grafana

Primeiro, recupere a URL do Grafana usando o comando abaixo:
```
oc get route -n tef | grep grafana
```
Use essa URL com o protocolo HTTP para acessar a interface do Grafana, como na iagem abaixo, use o login default "admin" e senha "admin", no primeiro login ocê será convidado a trocar a senha.
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/grafana01.png)

Após autenticado, você verá uma tela como a da imagem abaixo, clique no ícone de "configurações" no menu lateral esquerdo e escolha a opção "Data Sources":
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/grafana02.png)

Recupere o Token de acesso ao Prometheus (atraves do Thanos)
```
oc sa get-token -n tef tef
``` 

Na tela que abrir clique no botão "Add data source", escolha a opção "Prometheus". Na tela que abrir informe a URL do Prometheus: "https://thanos-querier.openshift-monitoring.svc:9091". 
Desligue a verificação de SSL (Skip TLS verify). Adicione um Header HTTP com o ID "Authorization" e valor "Bearer "  + o token que recuperamos na etapa anterior. 

Clique em "Save & Test"
Uma mensagem de sucesso em verde deve aparecer ao final da tela
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/grafana03.png)

#### 4.1.2 Importando Dashboard
 
Ainda autenticado no Grafana, vá ao menu lateral esquerdo no sinal de "+" e escolha a opção "Import" como na imagem abaixo: 
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/grafana04.png)

Clique no botão "Upload JSON File" e escolha o arquivo grafana_dashboard_20200506.json que esta na pasta "Gm4cArtefatos/Grafana" deste repositório. Clique no botão "Import" para concluir a importação. 
![alt text](https://github.com/richardseberino/AcademiaSRE/blob/main/Gm4cArtefatos/images/grafana05.png)

## 5.0 Testando a aplicação

