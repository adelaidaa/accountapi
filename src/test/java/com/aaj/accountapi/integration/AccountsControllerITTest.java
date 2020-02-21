package com.aaj.accountapi.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.http.ContentType;
import io.restassured.matcher.ResponseAwareMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static io.restassured.RestAssured.given;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {WireMockInitializer.class})
public class AccountsControllerITTest {

    @Autowired
    private WireMockServer wireMockServer;

    @LocalServerPort
    private Integer port;

    @AfterEach
    public void afterEach() {
        this.wireMockServer.resetAll();
    }

    @Test
    public void testPostAccountReturnOk() throws Exception {
        String accountRequest = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("stubs/accounts/accountRequest.json").toURI())));
        stubForPostAccount();

        given()
                .contentType(ContentType.JSON)
                .body(accountRequest)
                .when()
                    .post("http://localhost:" + port + "/accounts")
                .then()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("id", Matchers.equalTo("ae27e265-9605-4b4b-a0e5-3003ea9cc4dc"))
                    .body("country", Matchers.equalTo("GB"))
                    .body("currency", Matchers.equalTo("GBP"))
                    .body("accountNumber", Matchers.equalTo("41426819"))
                    .body("bic", Matchers.equalTo("NWBKGB22"))
                    .body("iban", Matchers.equalTo("GB11NWBK40030041426819"))
                    .body("accountName", Matchers.equalTo("Samantha Holder"))
                .assertThat()
                    .statusCode(201);

    }

    @Test
    public void testPostAccountThirdPartyReturnException() throws Exception {
        String accountRequest = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("stubs/accounts/accountRequest.json").toURI())));
        stubForPostAccountException();

        given()
                .contentType(ContentType.JSON)
                .body(accountRequest)
                .when()
                    .post("http://localhost:" + port + "/accounts")
                .then()
                    .body("message", Matchers.equalTo("Third Party Exception"))
                    .body("details", Matchers.hasSize(1))
                    .body("details", Matchers.hasItem("400 Bad Request: [no body]"))
                .assertThat()
                    .statusCode(400);
    }

    private void stubForPostAccount() throws Exception {
        String accountRequest = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("stubs/accounts/form3AccountRequest.json").toURI())));
        String accountResponse = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("stubs/accounts/form3AccountResponse.json").toURI())));

        this.wireMockServer.stubFor(
                WireMock.post("/v1/organisation/accounts")
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(accountRequest))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(accountResponse)));
    }

    private void stubForPostAccountException() throws Exception {
        this.wireMockServer.stubFor(
                WireMock.post("/v1/organisation/accounts")
                        .willReturn(aResponse()
                                .withStatus(400)));
    }

}