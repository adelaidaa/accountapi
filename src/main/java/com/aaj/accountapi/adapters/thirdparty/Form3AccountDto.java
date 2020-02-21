package com.aaj.accountapi.adapters.thirdparty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@Getter
@Setter
public class Form3AccountDto {
    private Form3Account data;

    public Form3AccountDto(Form3Account data) {
        this.data = data;
    }
}
