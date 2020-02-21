package com.aaj.accountapi.core.ports.accounts;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@EqualsAndHashCode
public class AccountDto {
    private UUID id;
    private String country;
    private String currency;
    private String accountNumber;
    private String bic;
    private String iban;
    private String accountName;
}
