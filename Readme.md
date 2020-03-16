# BBC Radio Programs Retriever

## CSC 575 - Intelligent Information Retrieval
### Nardos Tessema

### Modules
1. commons
2. crawler
3. documents
4. preprocessor
5. query-processor
6. indexer
7. spring-boot-ui

### Environment Variable Needed
```
CORPORA_DIR
```
This environment variable is the directory in which the corpora are saved.

### Setting the Environment Variable

Run in a directory that is the same level as ```corpora```

Windows: ```C:>SET CORPORA_DIR=corpora\```

Mac OSX/Linux: ```$export CORPORA_DIR=corpora/```
### Packaging

Go to the project home and run
```$mvn clean package```

### JAR Files Produced
```
├── commons-1.0-SNAPSHOT.jar
├── crawler-1.0-SNAPSHOT-jar-with-dependencies.jar
├── crawler-1.0-SNAPSHOT.jar
├── documents-1.0-SNAPSHOT.jar
├── indexer-1.0-SNAPSHOT.jar
├── preprocessor-1.0-SNAPSHOT.jar
├── query-processor-1.0-SNAPSHOT.jar
├── spring-boot-ui-1.0.jar

```
### Running the Crawler
- The ```CORPORA_DIR``` environment variable must be set.
- The directory ```$CORPORA_DIR/bbc``` must exist.

```
$java -jar crawler-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Running the Main Application

- The ```CORPORA_DIR``` environment variable must be set.
- The ```bbc``` corpus documents must be in ```$CORPORA_DIR/bbc``` 
- Port 8282 must be free.

```$java -jar spring-boot-ui-1.0.jar```

### Using the Application

Using a web browser such as Chrome, browse to

```http://localhost:8282```

