FROM alpine:3.22
COPY ./ .
RUN apk add openjdk17
RUN ./gradlew compileKotlin
ENTRYPOINT ./gradlew run