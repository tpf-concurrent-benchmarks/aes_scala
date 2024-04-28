REMOTE_WORK_DIR = aes_scala/aes_scala
SERVER_USER = efoppiano
SERVER_HOST = atom.famaf.unc.edu.ar

N_THREADS=4
REPEAT=100
PLAIN_TEXT=data/input.txt
ENCRYPTED_TEXT=data/encrypted.txt
DECRYPTED_TEXT=data/decrypted.txt

init:
	docker swarm init || true
.PHONY: init

build:
	docker rmi aes_scala-f || true
	docker build -t aes_scala -f ./Dockerfile ./
.PHONY: build

setup: init build
.PHONY: setup

dummy_file:
	mkdir -p data
	echo "Hello World!" > data/input.txt

deploy_local:
	docker compose -f=docker-compose-dev.yml up

_deploy:
	mkdir -p graphite
	mkdir -p grafana_config
	until \
	N_THREADS=$(N_THREADS) \
	REPEAT=$(REPEAT) \
	PLAIN_TEXT=$(PLAIN_TEXT) \
	ENCRYPTED_TEXT=$(ENCRYPTED_TEXT) \
	DECRYPTED_TEXT=$(DECRYPTED_TEXT) \
	docker stack deploy \
	-c docker-compose.yml \
	aes_scala; \
	do sleep 1; done
.PHONY: _deploy

deploy: remove build
	make _deploy
.PHONY: deploy

remove:
	if docker stack ls | grep -q aes_scala; then \
            docker stack rm aes_scala; \
	fi
.PHONY: remove

logs:
	docker service logs -f aes_scala_app
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