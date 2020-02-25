# springboot-accountapi-app

## Requirements

For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)

## Running the application locally

Execute the `main` method in the `com.aaj.accountapi.AccountApiApplication` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

If you want to run the tests using maven
```shell
mvn clean test
```

Tests are compose by unit tests, Integration tests using Wiremock and Integration tests using testcontainers and 
the docker-compose.yml provided to run the Container integration tests against the form3 account API provided.
Please see `com.aaj.accountapi.integration.docker.AccountsContainersITTest`

The `spring-boot-accountapi` has been dockerized and added to the `docker-compose.yml`, in order to boot the service
and to be able to do any manual tests that want to be performed. Tests have been skipped when building the docker image
for the spring-boot service `-DskipTests` and the only way to run all tests is via maven or the IDE.

Swagger has been added to document the API and can be accessed locally on:
`http://localhost:8081/swagger-ui.html`

The code has been structured using the ports and adapters - hexagonal architecture:
`https://softwarecampament.wordpress.com/portsadapters/`

In oder to allow for new adapters in the future to interact with the accounts API, 
right now the only adapter is the service Rest API to create, list and delete Accounts
but we potentially add other adapters like an async consumer of accounts from a topic. 

In any case I would have love to spend more time productionising my spring-boot service, adding a pipeline, 
CDC testing , metrics and monitoring, and also configure a circuit Breaker using Hystrix on the Accounts API
client in order to not take down the accountapi service provided 
but I think at this stage it was out of the scope of the exercise. 
 
## Copyright

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.