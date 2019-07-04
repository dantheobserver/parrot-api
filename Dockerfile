FROM clojure:tools-deps-alpine
WORKDIR /app

COPY deps.edn .
COPY src/ ./src
EXPOSE 3030
RUN clojure -Stree