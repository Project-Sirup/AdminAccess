FROM eclipse-temurin:17-jdk-jammy

WORKDIR /sirup/service

COPY ./target /sirup/service/target
COPY ./run.sh /sirup/service/run.sh

RUN chmod +x run.sh

CMD ["tail","-F","anything"]