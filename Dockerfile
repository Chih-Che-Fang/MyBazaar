FROM openjdk:7
COPY . /usr/src/mybazaar
WORKDIR /usr/src/mybazaar
RUN find /usr/src/mybazaar/src -type f -name "*.java"  > sources-docker.txt
RUN javac -cp "/usr/src/mybazaar/libs/*" -d "/usr/src/mybazaar/bin" -s "/usr/src/mybazaar/bin" @sources-docker.txt
ENTRYPOINT ["java", "-Xmx500m", "-cp", "/usr/src/mybazaar/bin:/usr/src/mybazaar/libs/*", "roles.Person"]
