package com.aaj.accountapi.adapters.thirdparty;

import com.aaj.accountapi.core.ports.accounts.AccountAttributes;
import com.aaj.accountapi.core.ports.accounts.AccountDto;
import com.aaj.accountapi.core.ports.accounts.AccountRequest;
import com.aaj.accountapi.core.ports.accounts.AccountType;
import com.aaj.accountapi.core.ports.accounts.AccountsPage;
import com.aaj.accountapi.core.ports.accounts.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class Form3AccountsClientTest {
    private static final String ACCOUNT_NUMBER = "41426819";
    private static final String BANK_ID = "400300";
    private static final String BANK_ID_CODE = "GBDSC";
    private static final String BIC = "NWBKGB22";
    private static final String IBAN = "GB11NWBK40030041426819";
    private static final String TITLE = "Ms";
    private static final String FIRST_NAME = "Samantha";
    private static final String BANK_ACCOUNT_NAME = "Samantha Holder";
    private static final HashSet<String> ALTERNATIVE_BANK_ACCOUNT_NAMES = new HashSet<>(Arrays.asList(" Sam Holder"));
    private static final String ACCOUNT_CLASSIFICATION = "Personal";
    private static final boolean JOINT_ACCOUNT = false;
    private static final boolean ACCOUNT_MATCHING_OPT_OUT = false;
    private static final String SECONDARY_IDENTIFICATION = "A1B2C3D4";
    private static final Country COUNTRY = Country.GB;
    private static final Currency BASE_CURRENCY = Currency.getInstance(Locale.UK);
    private static final UUID ID = UUID.randomUUID();
    private static final UUID ORGANISATION_ID = UUID.randomUUID();

    private ArgumentCaptor<URI> uriArgumentCaptor = ArgumentCaptor.forClass(URI.class);
    @Mock
    private RestTemplate restTemplate;

    private AccountRequest accountRequest;
    private Form3AccountDto form3AccountDto;
    private Form3AccountsDto form3AccountsDto;

    private Form3AccountsClient client;

    @BeforeEach
    private void setup() {
        client = new Form3AccountsClient(restTemplate, "localhost", "8080");
        accountRequest = AccountRequest.builder()
                .accountType(AccountType.ACCOUNTS)
                .attributes(AccountAttributes
                        .builder()
                        .country(COUNTRY)
                        .baseCurrency(BASE_CURRENCY)
                        .accountNumber(ACCOUNT_NUMBER)
                        .bankId(BANK_ID)
                        .bankIdCode(BANK_ID_CODE)
                        .bic(BIC)
                        .iban(IBAN)
                        .title(TITLE)
                        .firstName(FIRST_NAME)
                        .bankAccountName(BANK_ACCOUNT_NAME)
                        .alternativeBankAccountNames(ALTERNATIVE_BANK_ACCOUNT_NAMES)
                        .accountClassification(ACCOUNT_CLASSIFICATION)
                        .jointAccount(JOINT_ACCOUNT)
                        .accountMatchingOptOut(ACCOUNT_MATCHING_OPT_OUT)
                        .secondaryIdentification(SECONDARY_IDENTIFICATION)
                        .build())
                .id(ID)
                .organisationId(ORGANISATION_ID)
                .build();

        Form3Account form3Account = Form3Account.builder()
                .id(ID)
                .organisationId(ORGANISATION_ID)
                .attributes(Form3AccountAttributes.builder()
                        .country(COUNTRY.toString())
                        .baseCurrency(BASE_CURRENCY.toString())
                        .accountNumber(ACCOUNT_NUMBER)
                        .bankId(BANK_ID)
                        .bankIdCode(BANK_ID_CODE)
                        .bic(BIC)
                        .iban(IBAN)
                        .title(TITLE)
                        .firstName(FIRST_NAME)
                        .bankAccountName(BANK_ACCOUNT_NAME)
                        .alternativeBankAccountNames(ALTERNATIVE_BANK_ACCOUNT_NAMES)
                        .accountClassification(ACCOUNT_CLASSIFICATION)
                        .jointAccount(JOINT_ACCOUNT)
                        .accountMatchingOptOut(ACCOUNT_MATCHING_OPT_OUT)
                        .secondaryIdentification(SECONDARY_IDENTIFICATION).build()).build();

        form3AccountDto = Form3AccountDto.builder()
                .data(form3Account)
                .build();

        form3AccountsDto = Form3AccountsDto.builder()
                .data(Arrays.asList(form3Account))
                .links(PageLinks.builder()
                        .first("/v1/organisation/accounts?page%5Bnumber%5D=first&page%5Bsize%5D=1")
                        .last("/v1/organisation/accounts?page%5Bnumber%5D=last&page%5Bsize%5D=1")
                        .next("/v1/organisation/accounts?page%5Bnumber%5D=2&page%5Bsize%5D=1")
                        .self("/v1/organisation/accounts?page%5Bnumber%5D=1&page%5Bsize%5D=1")
                        .prev("/v1/organisation/accounts?page%5Bnumber%5D=0&page%5Bsize%5D=1")
                        .build())
                .build();
    }

    @Test
    void given__accountReceived__should_post_account() {
        ResponseEntity<Form3AccountDto> responseEntity = new ResponseEntity<>(form3AccountDto, HttpStatus.CREATED);
        given(restTemplate.postForEntity(any(URI.class), any(HttpEntity.class), eq(Form3AccountDto.class))).willReturn(responseEntity);

        AccountDto accountDto = client.accountReceived(accountRequest);

        verify(restTemplate).postForEntity(uriArgumentCaptor.capture(),  any(HttpEntity.class), eq(Form3AccountDto.class));
        assertThat(uriArgumentCaptor.getValue().toString()).isEqualTo("http://localhost:8080/v1/organisation/accounts");

        AccountDto expectedAccount = AccountDto.builder().id(ID)
                .country(COUNTRY.toString())
                .currency(BASE_CURRENCY.toString())
                .accountNumber(ACCOUNT_NUMBER)
                .accountName(BANK_ACCOUNT_NAME)
                .iban(IBAN)
                .bic(BIC)
                .build();
        assertThat(accountDto).isEqualTo(expectedAccount);
    }

    @Test
    void given__accountReceived__and_3rdparty_throwsHttpClientErrorException__should_throw_thirdPartyHttpException() {
        given(restTemplate.postForEntity(any(URI.class),
                any(HttpEntity.class),
                eq(Form3AccountDto.class))).willThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(ThirdPartyHttpException.class, () -> client.accountReceived(accountRequest));
    }

    @Test
    void given__accountReceived__and_3rdparty_throwsException__should_re_throw_exception() {
        given(restTemplate.postForEntity(any(URI.class),
                any(HttpEntity.class),
                eq(Form3AccountDto.class))).willThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> client.accountReceived(accountRequest));
    }

    @Test
    void given__accountId__should_get_existingAccount() {
        ResponseEntity<Form3AccountDto> responseEntity = new ResponseEntity<>(form3AccountDto, HttpStatus.OK);
        given(restTemplate.getForEntity(any(URI.class), eq(Form3AccountDto.class))).willReturn(responseEntity);

        AccountDto accountDto = client.findById(ID);

        verify(restTemplate).getForEntity(uriArgumentCaptor.capture(), eq(Form3AccountDto.class));
        assertThat(uriArgumentCaptor.getValue().toString()).isEqualTo("http://localhost:8080/v1/organisation/accounts/" + ID);

        AccountDto expectedAccount = AccountDto.builder().id(ID)
                .country(COUNTRY.toString())
                .currency(BASE_CURRENCY.toString())
                .accountNumber(ACCOUNT_NUMBER)
                .accountName(BANK_ACCOUNT_NAME)
                .iban(IBAN)
                .bic(BIC)
                .build();
        assertThat(accountDto).isEqualTo(expectedAccount);
    }

    @Test
    void given__findAccountId__and_3rdparty_throwsHttpClientErrorException__should_throw_thirdPartyHttpException() {
        given(restTemplate.getForEntity(any(URI.class),
                eq(Form3AccountDto.class))).willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(ThirdPartyHttpException.class, () -> client.findById(ID));
    }

    @Test
    void given__findAccountId__and_3rdparty_throwsException__should_re_throw_exception() {
        given(restTemplate.getForEntity(any(URI.class),
                eq(Form3AccountDto.class))).willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(RuntimeException.class, () -> client.findById(ID));
    }

    @Test
    void given__findAccounts__should_get_existingAccounts_and_pages() {
        ResponseEntity<Form3AccountsDto> responseEntity = new ResponseEntity<>(form3AccountsDto, HttpStatus.OK);
        given(restTemplate.getForEntity(any(URI.class), eq(Form3AccountsDto.class))).willReturn(responseEntity);

        AccountsPage accountsPage = client.findAccounts(1, 1);

        verify(restTemplate).getForEntity(uriArgumentCaptor.capture(), eq(Form3AccountsDto.class));
        assertThat(uriArgumentCaptor.getValue().toString()).isEqualTo("http://localhost:8080/v1/organisation/accounts?page%5Bnumber%5D=1&page%5Bsize%5D=1");

        AccountDto expectedAccount = AccountDto.builder().id(ID)
                .country(COUNTRY.toString())
                .currency(BASE_CURRENCY.toString())
                .accountNumber(ACCOUNT_NUMBER)
                .accountName(BANK_ACCOUNT_NAME)
                .iban(IBAN)
                .bic(BIC)
                .build();
        assertThat(accountsPage.getAccounts()).containsExactly(expectedAccount);
        assertThat(accountsPage.getFirstPage()).isEqualTo("/accounts?pageNumber=first&pageSize=1");
        assertThat(accountsPage.getLastPage()).isEqualTo("/accounts?pageNumber=last&pageSize=1");
        assertThat(accountsPage.getSelfPage()).isEqualTo("/accounts?pageNumber=1&pageSize=1");
        assertThat(accountsPage.getPreviousPage()).isEqualTo("/accounts?pageNumber=0&pageSize=1");
        assertThat(accountsPage.getNextPage()).isEqualTo("/accounts?pageNumber=2&pageSize=1");
    }

    @Test
    void given__findAccounts__and_3rdparty_throwsHttpClientErrorException__should_throw_thirdPartyHttpException() {
        given(restTemplate.getForEntity(any(URI.class), eq(Form3AccountsDto.class)))
                .willThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(ThirdPartyHttpException.class, () -> client.findAccounts(0,1));
    }

    @Test
    void given__findAccounts__and_3rdparty_throwsException__should_re_throw_exception() {
        given(restTemplate.getForEntity(any(URI.class), eq(Form3AccountsDto.class))).willThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> client.findAccounts(0,1));
    }

}