package com.example.account.dto;
import com.example.account.aop.AccountLockIdInterface;
import com.example.account.type.TransactionResultType;
import lombok.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class UseBalance {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor  // 이거 넣으면 돌아가는데 강사님 화면에는 없는 어노테이션

    public static class Request implements AccountLockIdInterface { // 겟터 있으니까 구현된것이나 마찬가지
        @NotNull    // 어떻게 밸리드 해야할지 달아준다
        @Min(1) // userId는 long인데 0은 없다고 가정
        private Long userId;

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;

        @NotNull
        @Min(10)
        @Max(1000_000_000)
        private Long amount;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{
        private String accountNumber;
        private TransactionResultType transactionResultType;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;

        public static Response from(TransactionDto transactionDto) {
            return  Response.builder()
                    .accountNumber(transactionDto.getAccountNumber())
                    .transactionResultType(transactionDto.getTransactionResultType())
                    .transactionId(transactionDto.getTransactionId())
                    .amount(transactionDto.getAmount())
                    .transactedAt(transactionDto.getTransactedAt())
                    .build();
        }
    }
}
