package com.example.account.dto;
import lombok.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
public class CreateAccount {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request{
        @NotNull    // 어떻게 밸리드 해야할지 달아준다
        @Min(1) // userId는 long인데 0은 없다고 가정
        private Long userId;

        @NotNull
        @Min(100)
        private Long initialBalance;    // 초기 잔고
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor  //
    @Builder
    public static class Response{
        private Long userId;
        private String accountNumber;
        private LocalDateTime registeredAt;

        public static Response from(AccountDto accountDto){ // dto에서 CreateAccount로 변환해준다.
            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .registeredAt(accountDto.getRegistererAt())
                    .build();
        }
    }
}
