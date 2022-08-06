package com.example.account.dto;
import com.example.account.domain.Account;
import com.example.account.domain.Transaction;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import lombok.*;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {

    private String accountNumber;   // useBalance의 Response에 AccountNumber가 있다.
    private TransactionType transactionType;
    private TransactionResultType transactionResultType;
    private Long amount;
    private Long balanceSnapshot;
    private String transactionId;   // id는 pk라서 직접 쓰면 위험하다.
    private LocalDateTime transactedAt;

    public static TransactionDto fromEntity(Transaction transaction){   // Transaction에서 DTO로 바꾼다.
        return  TransactionDto.builder()
                .accountNumber(transaction.getAccount().getAccountNumber())
                .transactionType(transaction.getTransactionType())
                .transactionResultType(transaction.getTransactionResultType())
                .amount(transaction.getAmount())
                .balanceSnapshot(transaction.getBalanceSnapshot())
                .transactionId(transaction.getTransactionId())
                .transactedAt(transaction.getTransactedAt())
                .build();
    }
}
