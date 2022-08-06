package com.example.account.dto;

import com.example.account.domain.Account;
import com.example.account.type.AccountStatus;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    private Long userId;
    private String accountNumber;
   // private AccountStatus accountStatus;
    private Long balance;
    private LocalDateTime registererAt;
    private LocalDateTime unregisteredAt;

    public static AccountDto fromEntity(Account account){
            return AccountDto.builder()
                    .userId(account.getAccountUser().getId())
                    .accountNumber(account.getAccountNumber())
                    .balance(account.getBalance())
//                             .accountStatus(account.getAccountStatus())
                    .registererAt(account.getRegisteredAt())
                    .unregisteredAt(account.getUnregisteredAt())
                    .build();
    }
}
