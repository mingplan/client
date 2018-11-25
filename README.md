# jenkins client

Trigger a jenkins job and get it's build log during building.

## Build artifact
``` shell
git clone https://github.com/mingplan/client.git
cd client
mvn clean compile package
```

## Run
``` shell
java -jar target/client-1.0-SNAPSHOT.jar org.ming.app.App
```
