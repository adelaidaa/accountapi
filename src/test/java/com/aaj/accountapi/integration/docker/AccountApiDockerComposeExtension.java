package com.aaj.accountapi.integration.docker;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.time.Duration;

public class AccountApiDockerComposeExtension implements BeforeAllCallback, AfterAllCallback {
    public DockerComposeContainer enviroment;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        enviroment = new DockerComposeContainer<>(new File("docker-compose.yml"))
                .withExposedService("accountapi", 8080,
                        Wait.forHttp("/v1/health")
                                .forStatusCode(200)
                                .withStartupTimeout(Duration.ofSeconds(160)))
                .withLocalCompose(true);
        enviroment.start();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        enviroment.stop();
    }
}
