package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.dto.AccountDto;
import com.example.account.dto.CreateAccount;
import com.example.account.dto.DeleteAccount;
import com.example.account.exception.AccountException;
import com.example.account.type.AccountStatus;
import com.example.account.service.AccountService;
import com.example.account.type.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// mock mvc를 만들어서 제공해준다.
@WebMvcTest (AccountController.class)
class AccountControllerTest {

    @MockBean   // 가짜로 빈 등록
    private AccountService accountService;


    // redisTestService, accountService가 AccountController안으로 주입된다.
    // 주입된 어플리케이션 상대로 mockMvc가 요청을 날려서 테스트
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;  // Obj <=> json

    @Test
    void successCreateAccount() throws Exception{
        // given
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDto.builder()
                .userId(1L)
                .accountNumber("12345678")
                .registererAt(LocalDateTime.now())
                .unregisteredAt(LocalDateTime.now())
                .build());
        // when

        // then
        // content에 문자 그대로 넣을 수도 있다. json형태로
        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new CreateAccount.Request(3333L, 1111L)
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                // 응답 바디에 오는 제이슨 경로에 받는다?
                .andExpect(jsonPath("$.accountNumber").value("12345678"))
                .andDo(print());
    }

    @Test
    void successDeleteAccount() throws Exception{
        // given
        given(accountService.deleteAccount(anyLong(), anyString()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registererAt(LocalDateTime.now())
                        .unregisteredAt(LocalDateTime.now())
                        .build());
        // when

        // then
        // content에 문자 그대로 넣을 수도 있다. json형태로
        mockMvc.perform(delete("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DeleteAccount.Request(3333L, "0987654321")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                // 응답 바디에 오는 제이슨 경로에 받는다?
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());
    }

    @Test
    void successGetAccount() throws Exception {
        // given
        given(accountService.getAccount(anyLong()))
                .willReturn(Account.builder()
                        .accountNumber("3456")
                        .accountStatus(AccountStatus.IN_USE)
                        .build());  // 모킹된 상태

        // when
        // then

        mockMvc.perform(get("/account/876"))    // 테스트 하려는 url
                .andDo(print())
                .andExpect(jsonPath("$.accountNumber").value("3456")) // 바디의 첫 번째 구조에 있는 값이
                .andExpect(jsonPath("$.accountStatus").value("IN_USE"))
                .andExpect(status().isOk());
    }

    @Test
    void successGetAccountsByUserId() throws Exception {
        // given
        List<AccountDto> accountDtos =
                Arrays.asList(
                        AccountDto.builder()
                                .accountNumber("1234567890")
                                .balance(1000L).build(),
                        AccountDto.builder()
                                .accountNumber("1111111111")
                                .balance(2000L).build(),
                        AccountDto.builder()
                                .accountNumber("2222222222")
                                .balance(3000L).build()
        );
        given(accountService.getAccountByUserId(anyLong()))
                .willReturn(accountDtos);
        // when

        // then
        mockMvc.perform(get("/account?user_id=1"))
                .andDo(print())
                .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
                .andExpect(jsonPath("$[0].balance").value(1000))
                .andExpect(jsonPath("$[1].accountNumber").value("1111111111"))
                .andExpect(jsonPath("$[1].balance").value(2000))
                .andExpect(jsonPath("$[2].accountNumber").value("2222222222"))
                .andExpect(jsonPath("$[2].balance").value(3000));
    }


    @Test
    void failGetAccount() throws Exception {
        // given
        given(accountService.getAccount(anyLong()))
                .willThrow(new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        // when
        // then
        mockMvc.perform(get("/account/876"))    // 테스트 하려는 url
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND")) // 바디의 첫 번째 구조에 있는 값이
                .andExpect(jsonPath("$.errorMessage").value("계좌가 없습니다."))
                .andExpect(status().isOk());
    }

}