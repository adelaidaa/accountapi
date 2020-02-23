package com.aaj.accountapi.integration.docker;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ExtendWith(AccountApiDockerComposeExtension.class)
public class AccountsContainersITTest {
    @LocalServerPort
    private Integer port;

    @Test
    public void testPostAccountReturnOk() throws Exception {
        String accountRequest = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("stubs/accounts/accountRequest.json").toURI())));

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

        deleteAccount("ae27e265-9605-4b4b-a0e5-3003ea9cc4dc");
    }

    @Test
    public void testGetAccountReturnOk() throws Exception {
        String accountRequest = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("stubs/accounts/accountRequest.json").toURI())));
        //Post account
        postAccount(accountRequest);

        //get account
        String accountId = "ae27e265-9605-4b4b-a0e5-3003ea9cc4dc";
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

        deleteAccount(accountId);
    }

    @Test
    public void testGetAccountReturnNotFound__when_account_not_existing() throws Exception {

        //get account
        String accountId = "ae27e265-9605-4b4b-a0e5-3003ea9cc4dc";
        given()
                .when()
                .get("http://localhost:" + port + "/accounts/" + accountId)
                .then()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void testGetAccountsReturnOk() throws Exception {
        //post 3 more accounts
        String accountRequest = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("stubs/accounts/accountRequest.json").toURI())));
        postAccount(accountRequest);
        String accountRequest1 = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("stubs/accounts/accountRequest1.json").toURI())));
        postAccount(accountRequest1);
        String accountRequest2 = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("stubs/accounts/accountRequest2.json").toURI())));
        postAccount(accountRequest2);
        given()
                .when()
                .get("http://localhost:" + port + "/accounts?pageNumber=1&pageSize=1")
                .then()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("accounts", hasSize(1))
                    .body("accounts[0].id", equalTo("ae27f265-9605-4b4b-a0e5-3003ea9cc4dc"))
                    .body("accounts[0].country", equalTo("GB"))
                    .body("accounts[0].currency", equalTo("GBP"))
                    .body("accounts[0].accountNumber", equalTo("41426819" ))
                    .body("accounts[0].bic", equalTo("NWBKGB22" ))
                    .body("accounts[0].iban", equalTo("GB11NWBK40030041426819" ))
                    .body("accounts[0].accountName", equalTo("Samantha Holder" ))
                    .body("previousPage", equalTo( "/accounts?pageNumber=0&pageSize=1"))
                    .body("nextPage", equalTo( "/accounts?pageNumber=2&pageSize=1"))
                    .body("firstPage", equalTo( "/accounts?pageNumber=first&pageSize=1"))
                    .body("lastPage", equalTo( "/accounts?pageNumber=last&pageSize=1"))
                    .body("selfPage", equalTo( "/accounts?pageNumber=1&pageSize=1"))
                .assertThat()
                    .statusCode(200);

        deleteAccount("ae27e265-9605-4b4b-a0e5-3003ea9cc4dc");
        deleteAccount("ae27f265-9605-4b4b-a0e5-3003ea9cc4dc");
        deleteAccount("af27f265-9605-4b4b-a0e5-3003ea9cc4dc");
    }

    private void postAccount(String accountRequest) {
        given()
                .contentType(ContentType.JSON)
                .body(accountRequest)
                .when()
                .post("http://localhost:" + port + "/accounts")
         .then()
                .assertThat()
                .statusCode(201);
    }

    private void deleteAccount(String accountId) {
        given()
                .when()
                .delete("http://localhost:" + port + "/accounts/" + accountId+ "?version=0")
                .then()
                .assertThat()
                .statusCode(204);
    }
}
