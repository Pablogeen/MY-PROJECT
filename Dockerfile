FROM amazoncorretto:21
WORKDIR /app
LABEL authors="BEN & CO"
COPY target/Me-0.0.1-SNAPSHOT.jar Me.jar
ENTRYPOINT ["java", "-jar", "Me.jar"]