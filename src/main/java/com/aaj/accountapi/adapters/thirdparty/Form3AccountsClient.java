package com.aaj.accountapi.adapters.thirdparty;

import com.aaj.accountapi.core.ports.ResourceNotFoundException;
import com.aaj.accountapi.core.ports.accounts.AccountAttributes;
import com.aaj.accountapi.core.ports.accounts.AccountDto;
import com.aaj.accountapi.core.ports.accounts.AccountRequest;
import com.aaj.accountapi.core.ports.accounts.AccountsClient;
import com.aaj.accountapi.core.ports.accounts.AccountsPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class Form3AccountsClient implements AccountsClient {

    private static final String V_1_ORGANISATION_ACCOUNTS = "/v1/organisation/accounts";
    private final RestTemplate restTemplate;
    private final UriComponentsBuilder uriBuilder;

    @Autowired
    public Form3AccountsClient(RestTemplate restTemplate,
                               @Value("${form3.base.url}") String form3BaseUrl,
                               @Value("${form3.base.port}") String form3BasePort) {
        this.restTemplate = restTemplate;
        this.uriBuilder = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(form3BaseUrl)
                .port(form3BasePort)
                .path(V_1_ORGANISATION_ACCOUNTS);
    }

    @Override
    public AccountDto findById(UUID id) throws ResourceNotFoundException {
        try {
            UriComponents uriComponents = uriBuilder.path("/{accountId}").buildAndExpand(id);
            ResponseEntity<Form3AccountDto> response = restTemplate.getForEntity(uriComponents.toUri(), Form3AccountDto.class);
            return toDto(response.getBody().getData());

        } catch (HttpClientErrorException e) {
            throw new ThirdPartyHttpException(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public AccountsPage findAccounts(int pageNumber, int pageSize) {
        try {
            uriBuilder
                    .queryParam("page[number]", String.valueOf(pageNumber))
                    .queryParam("page[size]", String.valueOf(pageSize));
            UriComponents uriComponents = uriBuilder.build().encode();
            ResponseEntity<Form3AccountsDto> response = restTemplate.getForEntity(uriComponents.toUri(), Form3AccountsDto.class);

            List<AccountDto> accountDtos = response.getBody().getData().stream()
                    .map(form3Account -> toDto(form3Account))
                    .collect(Collectors.toList());

            return AccountsPage.builder()
                    .firstPage(toDtoPage(response.getBody().getLinks().getFirst(), Pagetype.FIRST, pageNumber, pageSize))
                    .lastPage(toDtoPage(response.getBody().getLinks().getLast(), Pagetype.LAST, pageNumber, pageSize))
                    .selfPage(toDtoPage(response.getBody().getLinks().getSelf(), Pagetype.SELF, pageNumber, pageSize))
                    .previousPage(toDtoPage(response.getBody().getLinks().getPrev(), Pagetype.PREVIOUS, pageNumber, pageSize))
                    .nextPage(toDtoPage(response.getBody().getLinks().getNext(), Pagetype.NEXT, pageNumber, pageSize))
                    .accounts(accountDtos).build();

        } catch (HttpClientErrorException e) {
            throw new ThirdPartyHttpException(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public AccountDto accountReceived(AccountRequest accountRequest) throws ThirdPartyHttpException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Form3AccountDto> request = new HttpEntity<>(getForm3Account(accountRequest), headers);

        try {
            ResponseEntity<Form3AccountDto> response = restTemplate.postForEntity(uriBuilder.build().toUri(),
                    request, Form3AccountDto.class);
            return toDto(response.getBody().getData());

        } catch (HttpClientErrorException e) {
            throw new ThirdPartyHttpException(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }

    private String toDtoPage(String page, Pagetype pageType, int pageNumber, int pageSize) {
        if (page != null) {
            switch (pageType) {
                case FIRST:
                    return "/accounts?pageNumber=first&pageSize=" + pageSize;
                case LAST:
                    return "/accounts?pageNumber=last&pageSize=" + pageSize;
                case SELF:
                    return "/accounts?pageNumber=" + pageNumber + "&pageSize=" + pageSize;
                case NEXT:
                    int nextPage = pageNumber + 1;
                    return "/accounts?pageNumber=" + nextPage + "&pageSize=" + pageSize;
                case PREVIOUS:
                    int previousPage = pageNumber - 1;
                    return "/accounts?pageNumber=" + previousPage + "&pageSize=" + pageSize;
            }
        }
        return null;
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
                        .baseCurrency(attributes.getBaseCurrency() != null ? attributes.getBaseCurrency().toString() : null)
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
