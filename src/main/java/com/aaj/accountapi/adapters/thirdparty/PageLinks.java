package com.aaj.accountapi.adapters.thirdparty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class PageLinks {
    private String first;
    private String last;
    private String self;
    private String prev;
    private String next;
}
