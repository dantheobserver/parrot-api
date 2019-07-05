.PHONY: dev
dev:
	clojure -A:dev:nrepl

.PHONY: migrations
migrations: 
	clojure -A:dev:migrations

.PHONY: image
image:
	docker build -t parrot-server .

.PHONY: server
server:
	sudo docker run -p 3030:3030 parrot-server clojure -A:server 0.0.0.0 3030
