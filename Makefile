dev: dev/user.clj
	clojure -A:dev:nrepl
.PHONY: dev

image:
	docker build -t parrot-server .
.PHONY: image

server:
	sudo docker run -p 3030:3030 parrot-server clojure -A:server 0.0.0.0
.PHONY: server

