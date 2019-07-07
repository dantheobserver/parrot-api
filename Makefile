.PHONY: dev
dev:
	clojure -A:dev:nrepl

.PHONY: test
test:
	clojure -A:test:runner

.PHONY: migrate
migrate: 
	clojure -A:migrations -m "migrations" $(cmd)

.PHONY: image
image:
	docker build -t parrot-server .

.PHONY: server
server:
	sudo docker run -p 3030:3030 parrot-server clojure -A:server 0.0.0.0 3030
