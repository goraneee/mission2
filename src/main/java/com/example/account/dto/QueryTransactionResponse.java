package com.example.account.dto;

import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class QueryTransactionResponse {

    private String accountNumber;
    private TransactionResultType transactionResultType;
    private TransactionType transactionType;
    private String transactionId;
    private Long amount;
    private LocalDateTime transactedAt;
    private Long onlyForUse;
    private Long onlyForCancel;


    public static QueryTransactionResponse from(TransactionDto transactionDto) {
        return   QueryTransactionResponse.builder()
                .accountNumber(transactionDto.getAccountNumber())
                .transactionType(transactionDto.getTransactionType())
                .transactionResultType(transactionDto.getTransactionResultType())
                .transactionId(transactionDto.getTransactionId())
                .amount(transactionDto.getAmount())
                .transactedAt(transactionDto.getTransactedAt())
                .build();
    }
}
