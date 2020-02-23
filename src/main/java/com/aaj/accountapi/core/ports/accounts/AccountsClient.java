package com.aaj.accountapi.core.ports.accounts;

import java.util.UUID;

public interface AccountsClient {
    AccountDto findById(UUID id);

    AccountsPage findAccounts(int pageNumber, int pageSize);

    AccountDto accountReceived(AccountRequest accountRequest);

    void deleteAccount(UUID id, int version);

}
