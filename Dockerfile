# =====================================================
# Stage 1 : build
# =====================================================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

# On copie d'abord le pom.xml pour profiter du cache Docker
# lors du téléchargement des dépendances.
COPY pom.xml .

# Télécharge les dépendances Maven.
# Cela accélère les builds suivants si le pom.xml ne change pas.
RUN mvn -B dependency:go-offline

# Copie le code source.
COPY src ./src

# Construit le jar exécutable.
# Les tests sont déjà exécutés dans le pipeline GitHub Actions.
RUN mvn -B clean package -DskipTests

# =====================================================
# Stage 2 : runtime
# =====================================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copie seulement le jar produit par le stage de build.
COPY --from=build /build/target/finledger-cli-0.1.0.jar app.jar

# Crée un utilisateur non-root et le dossier de données.
RUN useradd --system --uid 1001 --user-group finledger \
    && mkdir -p /app/data \
    && chown -R finledger:finledger /app

USER finledger

# Les données de l'application seront persistées dans ce volume.
VOLUME /app/data

# Lance l'application CLI.
ENTRYPOINT ["java", "-jar", "app.jar"]