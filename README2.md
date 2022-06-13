
#####################################################################################

# Kafka

#####################################################################################

## Cria o container de Kfaka

```
docker run -d -p 2181:2181 -p 3030:3030 -p 8081-8083:8081-8083 -p 9581-9585:9581-9585 -p 9092:9092 --name=kafka  -e ADV_HOST=127.0.0.1 landoop/fast-data-dev:latest
```

## Se necessário Start do container
```
docker start kafka
```
## Entra no container do Kafka
```
docker exec -ti kafka bash
```
## Roda esses comandos para criar a fila
```
kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic tef

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic senha

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic conta

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic limite

exit
```



#####################################################################################

# Cassandra

#####################################################################################

Docker User/Pass: cassandra / cassandra

## Cria o container do cassandra
```
docker run --name cassandra -p 7000:7000 -p 9042:9042 -d cassandra:latest
```
## Se necessário Start do container
```
docker start cassandra
```
## Entrar no container do casandra 
```
docker exec -it cassandra bash
```
## inicia a sessão interativa com o banco de dados
```
cqlsh
```
## cria o banco de dados
```
create keyspace itau WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};
```
## utiliza o keyspace (itau) criado 
```
use itau;
```
## cria as tabelas ;
```
CREATE TABLE tef (id_tef uuid PRIMARY KEY, evento text, tipo text, agencia_origem int, conta_origem int, dv_origem int, agencia_destino int, conta_destino int, dv_destino int, timestamp timestamp, valor decimal, senha text, transactionId text, rc_simulacao text,msg_simulacao text, rc_senha text, msg_senha text, rc_limite text, msg_limite text, rc_credito text, msg_credito text, rc_debito text, msg_debito text, rc_efetivacao text, msg_efetivacao text);

CREATE TABLE senha (id_senha uuid PRIMARY KEY, agencia int, conta int, dv int, senha text);

CREATE TABLE limite(id_limite uuid PRIMARY KEY, agencia int, conta int, dv int, valor_limite decimal, valor_utilizado decimal, timestamp_limite text);

CREATE TABLE conta (id_conta uuid PRIMARY KEY, agencia int, conta int, dv int, valor_saldo decimal, bloqueio int);
```
## popula as tabeleas

```
INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 10, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 11, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 12, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 13, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 14, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 15, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 16, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 17, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 18, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 19, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 20, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 21, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 22, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 23, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 24, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 25, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 26, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 27, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 28, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 29, 0, '123456');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 10, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 11, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 12, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 13, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 14, 0, 0.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 15, 0, 0.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 16, 0, 0.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 17, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 18, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 19, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 20, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 21, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 22, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 23, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 24, 0, 0.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 25, 0, 0.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 26, 0, 0.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 27, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 28, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 29, 0, 1000000.00, 0.00, '2020-02-10 09:00:00.000');

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 10, 0, 1000000.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 11, 0, 1000000.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 12, 0, 1000000.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 13, 0, 1000000.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 14, 0, 1000000.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 15, 0, 0.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 16, 0, 0.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 17, 0, 0.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 18, 0, 0.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 19, 0, 0.0, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 20, 0, 1000000.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 21, 0, 1000000.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 22, 0, 1000000.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 23, 0, 1000000.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 24, 0, 1000000.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 25, 0, 0.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 26, 0, 0.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 27, 0, 0.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 28, 0, 0.00, 1);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 29, 0, 0.00, 1);

SELECT * FROM tef;

SELECT * FROM senha;

SELECT * FROM limite;

SELECT * FROM conta;

use system;       

select release_version from local;
```

#####################################################################################

# Jaeger

#####################################################################################

## Cria o container do Jaeger
```
docker run -d --name jaeger -e COLLECTOR_ZIPKIN_HTTP_PORT=9411  -p 5775:5775/udp  -p 6831:6831/udp  -p 6832:6832/udp  -p 5778:5778  -p 16686:16686    -p 14268:14268  -p 9411:9411  jaegertracing/all-in-one:1.16
```
## Se necessário Start do container
```
docker start jaeger
```
## Para entrar na Console do Jaeger: 
```
http://localhost:16686
```

#####################################################################################

# Prometheus

#####################################################################################

## Ajutar o arquivo prometheus.yaml com o IP atual da máquina

IPConfig

Notepad C:\Users\AlexandreZanettideAl\git\gm4c\Gm4cArtefatos\prometheus.yaml

## Cria o container do Prometheus
```
docker run -d --name=prometheus --cap-add SYS_TIME -P -v /Users/richardmarques/itau/gm4c/Gm4cArtefatos/Prometheus/prometheus.yaml:/etc/prometheus/prometheus.yml -p 9090:9090 prom/prometheus 

## Outro exemplo de diretório do arquivo prometheus.yml
```
docker run -d --name=prometheus --cap-drop CHOWN --cap-drop SETUID --cap-drop ALL --cap-add SYS_TIME -P -v C:\Users\AlexandreZanettideAl\git\gm4c\Gm4cArtefatos\Prometheus\prometheus.yml:/etc/prometheus/prometheus.yml -p 9090:9090 prom/prometheus

## Se necessário entrar no container
```
docker exec -it prometheus /bin/sh   
```

## Se necessário Start do container
```
docker start prometheus
```
## Para entrar na Console do Prometheus: 

http://localhost:9090/


#####################################################################################

# Splunk 

#####################################################################################

## Cria o container do Splunk 
```
docker run -d -p 8180:8000 -e "SPLUNK_START_ARGS=--accept-license" -e "SPLUNK_PASSWORD=Passw0rd" --name splunk splunk/splunk:latest
```
## Se necessário Start do container
```
docker start splunk
```
## Pull the plugin from docker hub

TBC docker plugin install splunk/docker-logging-plugin:latest --alias splunk-logging-plugin

## Enable the plugin if needed:

TBC docker plugin enable splunk-logging-plugin

## Para entrar na Console do splunk: 

http://localhost:8180/

admin / Passw0rd

## Instalar o Agente na máquina que está rodando a aplicação

https://docs.splunk.com/Documentation/Forwarder/8.0.2/Forwarder/InstallaWindowsuniversalforwarderfromaninstaller

## Deployment server MGMT Port

localhost:8089

## Receiver Indexer

localhost:9997

## Startando o Agent

set SPLUNK_HOME="C:\Program Files\SplunkUniversalForwarder"

cd %SPLUNK_HOME%\bin

.\splunk start
.\splunk restart

## Logs do Splunk Agent (Forwarder) 

C:\Program Files\SplunkUniversalForwarder\var\log\splunk


#####################################################################################

# Grafana

#####################################################################################

## Cria o container do Grafana
```
docker run -d --name=grafana -p 3000:3000 grafana/grafana
```
## Se necessário Start do container
```
docker start grafana
```
## Para entrar na Console do Jaeger: 

http://localhost:3000

#####################################################################################

# Redis

#####################################################################################

## Cria o container do Redis
```
docker run --name redis -p 6379:6379 -d redis:alpine
```
## Se necessário Start do container
```
docker start redis
```
## Entrar no container do Redis
```
docker exec -it redis bash
```
#####################################################################################

# Hazelcast

#####################################################################################

## Cria o container do Hazelcast
```
docker run --name hazelcast  -d -p 8080:8080 hazelcast/management-center
```
## Se necessário Start do container
```
docker start hazelcast
```
## Entrar no container do Hazelcast
```
docker exec -it hazelcast bash
```
#####################################################################################

# Spring Tool Suite

#####################################################################################

## ATENÇÃO!!! Baixar a versão 3.9

https://github.com/spring-projects/toolsuite-distribution/wiki/Spring-Tool-Suite-3

#####################################################################################

# Git Bash

#####################################################################################

## ATENÇÃO!!! Executar uma única vez:

   git config --global user.name "FIRST_NAME LAST_NAME"
   
   git config --global user.email "MY_NAME@example.com"


## ATENÇÃO!!! Executar Sempre:

1. Executar o Git Bash.
   Ex.: /c/Users/???/git/gm4c
        /c/users/AlexandreZanettideAl/git/gm4c
        
2. Entrar no diretório do Git do projeto (onde encontra o .GIT).

3. Baixar o repositório atualizado:

   git fetch
   
   git pull 
   
4. Fazer o Refresh + Project Clean no Eclipse (Spring Tool Suite)

5. Para subir uma alteração:

git add /c/users/{usuario}/git/gm4c/{arquivo}

Ou 

git add {diretorio}/src/main/java/com/gm4c/{xxx}/dto/{xxxRepositorio.java}

git commit -a -m "Alterado"

git push


6. Refazer o item 3 (Fetch + Pull)


#######################################

# Clonando o projeto do Git

#######################################

## 1a. opção: Entrar no Git Bash... 

git clone https://github.com/richardseberino/gm4c.git

Entrar no Spring Tool Suite (Eclipse do Spring Boot)

Entrar na Perspectiva de Git ==>  Add a Existent Git Repository ... ==> "Colar" o diretório "C:\Users\AlexandreZanettideAl\git" ==> Finish/OK

## 2a. opção: Entrar no Spring Tool Suite (Eclipse do Spring Boot) ... 

Entrar na Perspectiva de Git ==> Clone a Git repository ... ===> "Colar" o projeto "https://github.com/richardseberino/gm4c.git" ==> digitar seu usuário/senha do Git Hub (não o da IBM, o Git público). ==> Finish/OK 


#######################################

# Importando os projetos Maven no Spring Tool Suite (Eclipse do Spring Boot) 

#######################################

**(Atenção para essa documentação foi utilizada a versão 4 do STS)**

Entrar no Spring Tool Suite (Eclipse do Spring Boot) e clicar em Import Project
![Importing project](/Gm4cArtefatos/images/print2.png)

Selecione o diretório onde você fez o git clone ele deve lista-los e desmarque o primeiro projeto o gm4c esse processo deve demorar um pouco.
![Importing project](/Gm4cArtefatos/images/print3.png)

Adicione a lib do Junit5 para os projetos Senha e TEF, clique no botão direito no projeto **Senha** e selecione properties, vá até a aba libraries e clique no botão Add library
![Importing project](/Gm4cArtefatos/images/print4.png)

Selecione JUNIT e clique no botão next
![Importing project](/Gm4cArtefatos/images/print5.png)

Selecione Junit 5 e clique em finish
![Importing project](/Gm4cArtefatos/images/print6.png)

Após isso clique em apply and close
![Importing project](/Gm4cArtefatos/images/print7.png)

** Repita o mesmo processo para o projeto TEF**

4. Baixar todas as dependências do Mavne

  4.1. Maven Update ==> selecionar todos os projetos ==> Finish


5. Limpar o projeto do Eclipse

  5.1. Projeto ==> Clean ==> Install


#####################################################################################

# ETC HOSTS

#####################################################################################

## Incluir novos Hosts no ETC HOSTS

## Entrar no CMD.EXE como ADMINISTRADOR!

cd C:\Windows\System32\Drivers\etc\

ipconfig - Procurar ==> Wireless LAN adapter Wi-Fi ==> IPv4 Address: ?.?.?.?

notepad hosts

127.0.0.1 ... kafka-svc cassandra-svc prometheus-svc kafka-svc jaeger-svc redis-svc
?.?.?.?   tef-svc limite-svc senha-svc conta-svc spring-admin-svc



## E ajustar a linha abaixo (ou incluir caso não tenha):

127.0.0.1 {..os hosts j[a existentes..} tef-svc limite-svc senha-svc conta-svc spring-admin-svc kafka-svc cassandra-svc prometheus-svc


#####################################################################################
#####################################################################################

#####################################################################################

# START da máquina

#####################################################################################

## Git bash

cd /c/users/AlexandreZanettideAl/git/gm4c

git pull

git fetch


## CMD -- Start geral

docker start kafka

docker start cassandra

docker start jaeger

docker start prometheus

docker start splunk

docker start grafana

#####################################################################################
