FROM eclipse-temurin:21-jdk-alpine AS construcao

WORKDIR /aplicacao

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS execucao

RUN addgroup -S aplicacao && adduser -S aplicacao -G aplicacao

WORKDIR /aplicacao

COPY --from=construcao /aplicacao/target/desafio-itau-transacoes-0.0.1-SNAPSHOT.jar aplicacao.jar

USER aplicacao
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "aplicacao.jar"]
