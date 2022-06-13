sleep 10
cd /opt/kafka/bin
./kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 5 --topic senha
./kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 5 --topic conta
./kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 5 --topic limite
./kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 5 --topic tef
