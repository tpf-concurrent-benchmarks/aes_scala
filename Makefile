REMOTE_WORK_DIR = aes_scala/aes_scala
SERVER_USER = efoppiano
SERVER_HOST = atom.famaf.unc.edu.ar
N_THREADS=6

init:
	docker swarm init || true
.PHONY: init

build:
	docker rmi aes_scala-f || true
	docker build -t aes_scala -f ./Dockerfile ./
.PHONY: build

setup: init build
.PHONY: setup

deploy_local:
	N_THREADS=${N_THREADS} docker compose -f=docker-compose-dev.yml up

deploy: remove build
	mkdir -p graphite
	mkdir -p grafana_config
	N_THREADS=4 docker stack deploy \
	-c docker/server.yaml \
	aes_scala; \
	do sleep 1; done
.PHONY: deploy

remove:
	if docker stack ls | grep -q aes_scala; then \
            docker stack rm aes_scala; \
	fi
.PHONY: remove

logs:
	docker service logs -f aes_scala
.PHONY: logs

run_local:
	LOCAL=true sbt -J-Xmx500M run
.PHONY: run_manager_local

common_publish_local:
	sbt publishLocal
.PHONY: common_publish_local

# Server specific

## Use *_remote if you are running them from your local machine

deploy_remote:
	ssh $(SERVER_USER)@$(SERVER_HOST) 'cd $(REMOTE_WORK_DIR) && make deploy'
.PHONY: deploy_remote

remove_remote:
	ssh $(SERVER_USER)@$(SERVER_HOST) 'cd $(REMOTE_WORK_DIR) && make remove'
.PHONY: remove_remote

## Tunneling

tunnel_graphite:
	ssh -L 8080:127.0.0.1:8080 $(SERVER_USER)@$(SERVER_HOST)
.PHONY: tunnel_graphite

tunnel_cadvisor:
	ssh -L 8888:127.0.0.1:8888 $(SERVER_USER)@$(SERVER_HOST)
.PHONY: tunnel_cadvisor

tunnel_grafana:
	ssh -L 8081:127.0.0.1:8081 $(SERVER_USER)@$(SERVER_HOST)
.PHONY: tunnel_grafana

# Cloud specific

deploy_cloud: remove
	mkdir -p graphite
	mkdir -p grafana_config
	docker stack deploy \
	-c docker/server.yaml \
	aes_scala; \
	do sleep 1; done
.PHONY: deploy_cloud