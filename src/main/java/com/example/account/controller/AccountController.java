package com.example.account.controller;
import com.example.account.domain.Account;
import com.example.account.dto.AccountInfo;
import com.example.account.dto.CreateAccount;
import com.example.account.dto.DeleteAccount;
import com.example.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController // 컨트롤러가 빈으로 등록
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/account") // 최종 합의한 문서에서 post로 어카운트 할 거니까
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request){ // 요청이 바디로 들어온다.
        return  CreateAccount.Response.from(
                accountService.createAccount(
                request.getUserId(),
                request.getInitialBalance()));
    }

    @DeleteMapping("/account") // 최종 합의한 문서에서 post로 어카운트 할 거니까
    public DeleteAccount.Response deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request){ // 요청이 바디로 들어온다.
        return  DeleteAccount.Response.from(
                accountService.deleteAccount(
                request.getUserId(),
                request.getAccountNumber()));
    }

    @GetMapping("/account")
    public List<AccountInfo> getAccountByUserId(
            @RequestParam("user_id") Long userId
    ){
        return accountService.getAccountByUserId(userId)
                .stream().map(accountDto ->
                         AccountInfo.builder()
                        .accountNumber(accountDto.getAccountNumber())
                        .balance(accountDto.getBalance())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/account/{id}")
    public Account getAccount(
            @PathVariable Long id){
        return accountService.getAccount(id);
    }
}
