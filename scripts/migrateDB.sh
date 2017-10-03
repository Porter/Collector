mvn package

java -jar target/collector-1.0.jar db migrate config.yml
java -jar target/collector-1.0.jar db migrate test.yml
