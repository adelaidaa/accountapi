package com.aaj.accountapi.adapters.thirdparty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@EqualsAndHashCode
public class Form3Account {
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("organisation_id")
    private UUID organisationId;
    @JsonProperty("type")
    private String type;
    @JsonProperty("attributes")
    private Form3AccountAttributes attributes;
}
