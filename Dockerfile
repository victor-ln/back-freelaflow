FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

# 1. Copia apenas o pom.xml
COPY pom.xml .

# 2. Baixa as dependências E os plugins
# O comando 'dependency:go-offline' falha em pegar plugins dinâmicos.
# Adicionamos 'dependency:resolve-plugins' para garantir que o plugin do Spring Boot seja baixado.
RUN mvn dependency:go-offline -B && \
    mvn dependency:resolve-plugins -B

# 3. (Opcional) Copia o código fonte. 
# Como você usa volume no docker-compose, isso é útil apenas para builds de produção,
# mas não atrapalha o dev.
# COPY src ./src

EXPOSE 8080

# O comando de inicialização
CMD ["mvn", "spring-boot:run"]