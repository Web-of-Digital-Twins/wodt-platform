FROM alpine:3.20
COPY ./ .
RUN apk add openjdk17
RUN ./gradlew compileKotlin
ENTRYPOINT ./gradlew run