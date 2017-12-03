FROM openjdk:8-jdk

MAINTAINER Khaled Ayoubi khaled.ayoubi@gmail.com

RUN mkdir -p /opt/sentifi

ADD target/stockPriceService.jar /opt/sentifi/

EXPOSE 8080

CMD ["java", "-jar", "/opt/sentifi/stockPriceService.jar"]