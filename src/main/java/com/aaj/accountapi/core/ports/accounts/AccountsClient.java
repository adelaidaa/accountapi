package com.aaj.accountapi.core.ports.accounts;

import java.util.UUID;

public interface AccountsClient {
    AccountDto findById(UUID id);

    AccountDto accountReceived(AccountRequest accountRequest);
}
