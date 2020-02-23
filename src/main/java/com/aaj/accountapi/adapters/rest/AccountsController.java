package com.aaj.accountapi.adapters.rest;

import com.aaj.accountapi.core.ports.accounts.AccountDto;
import com.aaj.accountapi.core.ports.accounts.AccountRequest;
import com.aaj.accountapi.core.ports.accounts.AccountsClient;
import com.aaj.accountapi.core.ports.accounts.AccountsPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
public class AccountsController {
    private final AccountsClient accountsClient;

    @Autowired
    public AccountsController(AccountsClient accountsClient) {
        this.accountsClient = accountsClient;
    }

    @PostMapping(path = "/accounts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addAccount(@Valid @RequestBody AccountRequest accountRequest) {
        AccountDto accountDto = accountsClient.accountReceived(accountRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(accountDto.getId())
                .toUri();

        return ResponseEntity.created(location).contentType(MediaType.APPLICATION_JSON).body(accountDto);
    }

    @GetMapping(path = "/accounts/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAccountById(@PathVariable(value="accountId") UUID accountId) {
        AccountDto accountDto = accountsClient.findById(accountId);

        return ResponseEntity.ok(accountDto);
    }

    @GetMapping(path = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAccounts(@RequestParam int pageNumber,@RequestParam int pageSize) {
        AccountsPage accountsPage = accountsClient.findAccounts(pageNumber, pageSize);

        return ResponseEntity.ok(accountsPage);
    }
}
