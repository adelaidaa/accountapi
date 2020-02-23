package com.aaj.accountapi.adapters.rest;

import com.aaj.accountapi.core.ports.accounts.AccountDto;
import com.aaj.accountapi.core.ports.accounts.AccountRequest;
import com.aaj.accountapi.core.ports.accounts.AccountsClient;
import com.aaj.accountapi.core.ports.accounts.AccountsPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AccountsControllerTest {
    @MockBean
    private AccountsClient accountsClient;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void add_account_Created() throws Exception {
        //Load body
        String accountRequestString = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("stubs/accounts/accountRequest.json").toURI())));
        ObjectMapper mapper = new ObjectMapper();
        AccountRequest accountRequest = mapper.readValue(accountRequestString, AccountRequest.class);
        //Mocks
        AccountDto accountDto = AccountDto.builder().id(accountRequest.getId()).build();
        given(accountsClient.accountReceived(accountRequest)).willReturn(accountDto);

        //Given When Then
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(accountRequestString))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", equalTo("http://localhost/accounts/ae27e265-9605-4b4b-a0e5-3003ea9cc4dc")));
    }

    @Test
    public void get_account_byId() throws Exception {
        //Mocks
        UUID accountId = UUID.randomUUID();
        AccountDto accountDto = AccountDto.builder().id(accountId).build();
        given(accountsClient.findById(accountId)).willReturn(accountDto);

        //Given When Then
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/" + accountId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void get_accounts() throws Exception {
        //Mocks
        UUID accountId = UUID.randomUUID();
        AccountDto accountDto = AccountDto.builder().id(accountId).build();
        AccountsPage accountsPage =AccountsPage.builder().accounts(Arrays.asList(accountDto)).build();
        int pageSize = 1;
        int pageNumber = 0;
        given(accountsClient.findAccounts(pageNumber, pageSize)).willReturn(accountsPage);

        //Given When Then
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts?pageNumber=0&pageSize=1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}