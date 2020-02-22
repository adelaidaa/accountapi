package com.aaj.accountapi.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {WireMockInitializer.class})
public class AccountsControllerITTest {

    @Autowired
    private WireMockServer wireMockServer;

    @LocalServerPort
    private Integer port;

    private AccountThirdPartyAPIStubs accountThirdPartyAPIStubs;

    @BeforeEach
    public void setup() {
        accountThirdPartyAPIStubs = new AccountThirdPartyAPIStubs(wireMockServer);
    }

    @AfterEach
    public void afterEach() {
        this.wireMockServer.resetAll();
    }

    @Test
    public void testPostAccountReturnOk() throws Exception {
        String accountRequest = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("stubs/accounts/accountRequest.json").toURI())));
        accountThirdPartyAPIStubs.stubForPostAccount();

        given()
                .contentType(ContentType.JSON)
                .body(accountRequest)
                .when()
                .post("http://localhost:" + port + "/accounts")
                .then()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("id", equalTo("ae27e265-9605-4b4b-a0e5-3003ea9cc4dc"))
                    .body("country", equalTo("GB"))
                    .body("currency", equalTo("GBP"))
                    .body("accountNumber", equalTo("41426819"))
                    .body("bic", equalTo("NWBKGB22"))
                    .body("iban", equalTo("GB11NWBK40030041426819"))
                    .body("accountName", equalTo("Samantha Holder"))
                .assertThat()
                    .statusCode(201);

    }

    @Test
    public void testPostAccountThirdPartyReturnException() throws Exception {
        String accountRequest = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("stubs/accounts/accountRequest.json").toURI())));
        accountThirdPartyAPIStubs.stubForPostAccountException();

        given()
                .contentType(ContentType.JSON)
                .body(accountRequest)
                .when()
                .post("http://localhost:" + port + "/accounts")
                .then()
                    .body("message", equalTo("Third Party Exception"))
                    .body("details", hasSize(1))
                    .body("details", hasItem("400 Bad Request: [no body]"))
                .assertThat()
                    .statusCode(400);
    }

    @Test
    public void testGetAccountReturnOk() throws Exception {
        String accountId = "ae27e265-9605-4b4b-a0e5-3003ea9cc4dc";
        accountThirdPartyAPIStubs.stubForGetAccount(UUID.fromString(accountId));

        given()
                .when()
                .get("http://localhost:" + port + "/accounts/" + accountId)
                .then()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("id", equalTo(accountId))
                    .body("country", equalTo("GB"))
                    .body("currency", equalTo("GBP"))
                    .body("accountNumber", equalTo("41426819"))
                    .body("bic", equalTo("NWBKGB22"))
                    .body("iban", equalTo("GB11NWBK40030041426819"))
                    .body("accountName", equalTo("Samantha Holder"))
                .assertThat()
                    .statusCode(200);

    }


    @Test
    public void testGetAccountForNonExistingAccountThirdPartyReturnException() throws Exception {
        String accountId = "af27e265-9605-4b4b-a0e5-3003ea9cc4dc";
        accountThirdPartyAPIStubs.stubForGetAccountException(UUID.fromString(accountId));

        given()
                .when()
                .get("http://localhost:" + port + "/accounts/" + accountId)
                .then()
                    .body("message", equalTo("Third Party Exception"))
                    .body("details", hasSize(1))
                    .body("details", hasItem("404 Not Found: [{\"error_message\":\"record af27e265-9605-4b4e-a0f5-3003ea9cc4dc does not exist\"}]"))
                .assertThat()
                    .statusCode(404);
    }
}