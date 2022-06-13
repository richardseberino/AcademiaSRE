cd  C:\Users\AlexandreZanettideAl\git\gm4c\
cd  C:\Users\"Dennis Lopes Silva"\git\gm4c\
git pull
git fetch
# git diff master origin/master
docker start kafka
docker start cassandra
docker start jaeger
docker start prometheus
docker start splunk
docker start grafana
docker start redis
docker start hazelcast

pause
curl http://localhost:9000/actuator/health
curl http://localhost:9001/actuator/health
curl http://localhost:9002/actuator/health
curl http://localhost:9003/actuator/health
pause
