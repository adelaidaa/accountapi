package com.aaj.accountapi.core.ports.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Currency;
import java.util.Set;

@Data
@JsonDeserialize(builder = AccountAttributes.AccountAttributesBuilder.class)
@Builder(builderClassName = "AccountAttributesBuilder", toBuilder = true)
public class AccountAttributes {
    @NotNull(message = "country must not be null")
    @JsonProperty("country")
    private Country country;
    @NotNull(message = "baseCurrency must not be null")
    @JsonProperty("base_currency")
    private Currency baseCurrency;
    @JsonProperty("account_number")
    private String accountNumber;
    @JsonProperty("bank_id")
    private String bankId;
    @JsonProperty("bank_id_code")
    private String bankIdCode;
    @JsonProperty("bic")
    private String bic;
    @JsonProperty("iban")
    private String iban;
    @JsonProperty("title")
    private String title;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("bank_account_name")
    private String bankAccountName;
    @JsonProperty("alternative_bank_account_names")
    private Set<String> alternativeBankAccountNames;
    @JsonProperty("account_classification")
    private String accountClassification;
    @JsonProperty("joint_account")
    private Boolean jointAccount;
    @JsonProperty("account_matching_opt_out")
    private Boolean accountMatchingOptOut;
    @JsonProperty("secondary_identification")
    private String secondaryIdentification;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AccountAttributesBuilder {
    }
}
