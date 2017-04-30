FROM ubuntu
RUN apt-get update \
 && apt-get install -y openjdk-8-jre
ADD content /content
ADD build/libs/soccer-all-1.0-SNAPSHOT.jar /soccer-all.jar

CMD java -jar soccer-all.jar /soccer.properties
