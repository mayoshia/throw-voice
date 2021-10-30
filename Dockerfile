FROM maven:3.6-jdk-11 as builder

WORKDIR /app

COPY pom.xml .
RUN mvn -B de.qaware.maven:go-offline-maven-plugin:resolve-dependencies

COPY LICENSE .
COPY src src

ARG BUILD_DATE
ARG VCS_REF
ARG VERSION
RUN mvn -B -Dversion="${VERSION}" -Dtimestamp="${BUILD_DATE}" -Drevision="${VCS_REF}" package

FROM gcr.io/distroless/java:11
LABEL maintainer="Sergey Agulenko <sergey.agulenko@gmail.com>"

ARG BUILD_DATE
ARG VCS_REF
ARG VERSION
LABEL org.label-schema.build-date=$BUILD_DATE \
      org.label-schema.name="mayoshia-pawa" \
      org.label-schema.description="Custom version of Pawa discord bot." \
      org.label-schema.version=$VERSION \
      org.label-schema.schema-version="1.0"

ENTRYPOINT []

EXPOSE 8080

ENV APP_DIR /app
ENV VERSION $VERSION

WORKDIR $APP_DIR

COPY --from=builder /app/target/pawa-release/lib lib
COPY --from=builder /app/target/pawa-release/*.jar .

CMD ["java", "-cp", "*:lib/*", "tech.gdragon.App"]
