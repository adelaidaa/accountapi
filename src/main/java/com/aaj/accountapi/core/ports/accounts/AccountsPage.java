package com.aaj.accountapi.core.ports.accounts;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountsPage {
    private List<AccountDto> accounts;
    private String nextPage;
    private String previousPage;
    private String firstPage;
    private String lastPage;
    private String selfPage;

}
