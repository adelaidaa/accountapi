package com.aaj.accountapi.adapters.thirdparty;

import com.aaj.accountapi.core.ports.ResourceNotFoundException;
import com.aaj.accountapi.core.ports.accounts.AccountAttributes;
import com.aaj.accountapi.core.ports.accounts.AccountDto;
import com.aaj.accountapi.core.ports.accounts.AccountRequest;
import com.aaj.accountapi.core.ports.accounts.AccountsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class Form3AccountsClient implements AccountsClient {

    private final RestTemplate restTemplate;
    private final String form3Uri;

    @Autowired
    public Form3AccountsClient(RestTemplate restTemplate,
                               @Value("${form3.base.url}") String form3BaseUrl,
                               @Value("${form3.base.port}") String form3BasePort) {
        this.restTemplate = restTemplate;
        this.form3Uri = form3BaseUrl + ":" + form3BasePort;
    }

    @Override
    public AccountDto findById(UUID id) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public AccountDto accountReceived(AccountRequest accountRequest) throws ThirdPartyHttpException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Form3AccountDto> request = new HttpEntity<>(getForm3Account(accountRequest), headers);

        try {
            ResponseEntity<Form3AccountDto> response = restTemplate.postForEntity(form3Uri + "/v1/organisation/accounts",
                    request, Form3AccountDto.class);
            return toDto(response.getBody().getData());

        } catch (HttpClientErrorException e) {
            throw new ThirdPartyHttpException(e.getStatusCode(), e.getMessage());
        }
        catch (Exception e){
            throw e;
        }
    }

    private AccountDto toDto(Form3Account data) {
        return AccountDto.builder()
                .id(data.getId())
                .country(data.getAttributes().getCountry())
                .currency(data.getAttributes().getBaseCurrency())
                .accountNumber(data.getAttributes().getAccountNumber())
                .bic(data.getAttributes().getBic())
                .iban(data.getAttributes().getIban())
                .accountName(data.getAttributes().getBankAccountName())
                .build();
    }

    private Form3AccountDto getForm3Account(AccountRequest accountRequest) {
        AccountAttributes attributes = accountRequest.getAttributes();
        Form3Account form3Account = Form3Account.builder()
                .id(accountRequest.getId())
                .organisationId(accountRequest.getOrganisationId())
                .type(accountRequest.getAccountType().toString().toLowerCase())
                .attributes(Form3AccountAttributes.builder()
                        .country(attributes.getCountry().toString())
                        .baseCurrency(attributes.getBaseCurrency() != null ? attributes.getBaseCurrency().toString() : null )
                        .accountNumber(attributes.getAccountNumber())
                        .bankId(attributes.getBankId())
                        .bankIdCode(attributes.getBankIdCode())
                        .bic(attributes.getBic())
                        .iban(attributes.getIban())
                        .title(attributes.getTitle())
                        .firstName(attributes.getFirstName())
                        .bankAccountName(attributes.getBankAccountName())
                        .alternativeBankAccountNames(attributes.getAlternativeBankAccountNames())
                        .accountClassification(attributes.getAccountClassification())
                        .jointAccount(attributes.getJointAccount())
                        .accountMatchingOptOut(attributes.getAccountMatchingOptOut())
                        .secondaryIdentification(attributes.getSecondaryIdentification())
                        .build())
                .build();

        return Form3AccountDto.builder().data(form3Account).build();
    }
}
