package com.aaj.accountapi.core.ports.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@JsonDeserialize(builder = AccountRequest.AccountRequestBuilder.class)
@Builder(builderClassName = "AccountRequestBuilder", toBuilder = true)
public class AccountRequest {
    @NotNull(message = "id must not be null")
    private UUID id;
    @NotNull(message = "organisationId must not be null")
    @JsonProperty("organisation_id")
    private UUID organisationId;
    @NotNull(message = "account type must not be null")
    @JsonProperty("type")
    private AccountType accountType;
    @NotNull(message = "attributes must not be null")
    private AccountAttributes attributes;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AccountRequestBuilder {
    }

}
