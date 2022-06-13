oc create namespace tef
oc project tef
oc create -f Gm4cArtefatos/openshift/service-account.yaml -n tef
oc adm policy add-scc-to-user privileged -z tef -n tef
oc create -f Gm4cArtefatos/openshift/kafka.yaml -n tef
oc create -f Gm4cArtefatos/openshift/kafka-svc.yaml -n tef
sleep 60
oc exec -ti kafka-0 -n tef -- kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic tef
oc exec -ti kafka-0 -n tef -- kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic senha
oc exec -ti kafka-0 -n tef -- kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic conta
oc exec -ti kafka-0 -n tef -- kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic limite
oc create -f Gm4cArtefatos/openshift/cassandra.yaml -n tef
oc create -f Gm4cArtefatos/openshift/cassandra-svc.yaml -n tef
sleep 120
oc cp Gm4cArtefatos/openshift/cassandra.sql cassandra-0:/tmp -n tef
oc exec -ti cassandra-0 -n tef -- cqlsh -u cassandra -p cassandra -f /tmp/cassandra.sql
