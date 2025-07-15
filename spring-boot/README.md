
# Setup
Update the project id in application.properties

spring.ai.vertex.ai.gemini.project-id=<id>

Login using terminal
gcloud auth application-default login

Run spring boot
./mvnw spring-boot:run

Basic Chat Request:

```Bash
curl -X POST -H "Content-Type: text/plain" -d "Hello Gemini, how are you?" http://localhost:8080/api/chat
```
Test reverseString Tool:

```Bash

curl -X POST -H "Content-Type: text/plain" -d "Can you please reverse the word 'SpringAI'?" http://localhost:8080/api/chat
```
Test getCurrentTimeAndLocation Tool:

```Bash

curl -X POST -H "Content-Type: text/plain" -d "What is the current time and our present location?" http://localhost:8080/api/chat
```
Test squareNumber Tool:

```Bash

curl -X POST -H "Content-Type: text/plain" -d "Calculate the square of 15." http://localhost:8080/api/chat
```


## Docker
### Build image

```Bash
cd spring-boot
docker build -t my-springboot-app .
```
### Run container
```Bash
docker run -e GOOGLE_APPLICATION_CREDENTIALS_JSON="$(cat vertex-ia-key.json)" -p 8080:8080 my-springboot-app
```

Where:`vertex-ia-key.json` is the vertex-ai-accessor key from GCP



