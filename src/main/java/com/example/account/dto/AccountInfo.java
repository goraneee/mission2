package com.example.account.dto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountInfo {  // 특정 정보를

    private String accountNumber;
    private Long balance;


}
