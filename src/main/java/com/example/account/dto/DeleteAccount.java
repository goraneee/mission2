package com.example.account.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class DeleteAccount {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{
        @NotNull    // 어떻게 밸리드 해야할지 달아준다
        @Min(1) // userId는 long인데 0은 없다고 가정
        private Long userId;

        @NotBlank    // NotNull 보다 강력
        @Size(min = 10, max = 10) // 문자열 길이를 확인해준다.
        private String accountNumber;    //
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{
        private Long userId;
        private String accountNumber;
        private LocalDateTime unRegisteredAt;

        public static Response from(AccountDto accountDto){ // dto에서 CreateAccount로 변환해준다.
            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .unRegisteredAt(accountDto.getUnregisteredAt())
                    .build();
        }
    }
}
