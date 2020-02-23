package com.aaj.accountapi.adapters.thirdparty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

@Builder
@Getter
@EqualsAndHashCode
public class Form3AccountAttributes {
     @JsonProperty("country")
     private String country;
     @JsonProperty("base_currency")
     private String baseCurrency;
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

}
