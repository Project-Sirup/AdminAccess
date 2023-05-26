FROM eclipse-temurin:17-jdk-jammy

WORKDIR /sirup/service

COPY ./target /sirup/service/target

CMD ["tail","-F","anything"]