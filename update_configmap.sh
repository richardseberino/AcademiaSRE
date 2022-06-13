git pull
kubectl delete configmap conta-application
kubectl delete configmap senha-application
kubectl delete configmap tef-application
kubectl delete configmap limite-application
kubectl delete configmap kibana-yml
kubectl delete configmap prometheus-yml
kubectl delete configmap kafka-export-conf

kubectl create configmap conta-application --from-file=/root/gm4c/Gm4cConta/src/main/resources/application.yml
kubectl create configmap senha-application --from-file=/root/gm4c/Gm4cSenha/src/main/resources/application.yml
kubectl create configmap tef-application --from-file=/root/gm4c/Gm4cTEF/src/main/resources/application.yml
kubectl create configmap limite-application --from-file=/root/gm4c/Gm4cLimite/src/main/resources/application.yml
kubectl create configmap kibana-yml --from-file=/root/gm4c/Gm4cArtefatos/kubernetes/configs/kibana/kibana.yml
kubectl create configmap prometheus-yml --from-file=/root/gm4c/Gm4cArtefatos/Prometheus/prometheus.yml
kubectl create configmap kafka-export-conf --from-file=/root/gm4c/Gm4cArtefatos/kubernetes/configs/kafkaExporter/application.conf

kubectl get pods | grep gm4c- | awk '{ print "kubectl delete pod " $1} '| bash
