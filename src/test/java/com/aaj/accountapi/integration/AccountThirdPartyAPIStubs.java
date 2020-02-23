package com.aaj.accountapi.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.MediaType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;

public class AccountThirdPartyAPIStubs {
    private final WireMockServer wireMockServer;

    public AccountThirdPartyAPIStubs(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
    }

    public void stubForPostAccount() throws Exception {
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

    public void stubForGetAccount(UUID accountId) throws Exception {
        String accountResponse = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("stubs/accounts/form3AccountResponse.json").toURI())));

        this.wireMockServer.stubFor(
                WireMock.get("/v1/organisation/accounts/"+ accountId)
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(accountResponse)));
    }

    public void stubForGetAccounts(String pageNumber, String pageSize) throws Exception {
        String accountsResponse = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("stubs/accounts/form3AccountsResponse.json").toURI())));

        this.wireMockServer.stubFor(
                WireMock.get("/v1/organisation/accounts?page%5Bnumber%5D="+pageNumber+"&page%5Bsize%5D="+pageSize)
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(accountsResponse)));
    }

    public void stubForPostAccountException() throws Exception {
        this.wireMockServer.stubFor(
                WireMock.post("/v1/organisation/accounts")
                        .willReturn(aResponse()
                                .withStatus(400)));
    }

    public void stubForGetAccountException(UUID accountId) throws Exception {
        this.wireMockServer.stubFor(
                WireMock.get("/v1/organisation/accounts/"+ accountId)
                        .willReturn(aResponse()
                                .withStatus(404).withBody("{\"error_message\":\"record af27e265-9605-4b4e-a0f5-3003ea9cc4dc does not exist\"}")));
    }
}
