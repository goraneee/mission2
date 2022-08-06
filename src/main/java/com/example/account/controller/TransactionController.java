package com.example.account.controller;

import com.example.account.aop.AccountLock;
import com.example.account.dto.*;
import com.example.account.exception.AccountException;
import com.example.account.service.TransactionService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 잔액 관련 컨트롤러
 * 1. 잔액 사용
 * 2. 잔액 사용 취소
 * 3. 거래 확인
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transaction/use")
    @AccountLock
    public UseBalance.Response useBalance(
            @Valid @RequestBody UseBalance.Request request
    )throws  InterruptedException {

        try { // 거래 성공

            Thread.sleep(3000L);
            return UseBalance.Response.from(
                    transactionService.useBalance(request.getUserId(),
                            request.getAccountNumber(), request.getAmount())
            );

        } catch (AccountException e) {  // 거래 실패
            log.error("Failed to use balance." + e.getMessage());

            transactionService.saveFailedUseTransaction(    // 실패건만 저장한다.
                    request.getAccountNumber(),
                    request.getAmount()
            );
            throw e;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/transaction/cancel")
    @AccountLock
    public CancelBalance.Response cancelBalance(
            @Valid @RequestBody CancelBalance.Request request
    ) {

        try { // 거래 성공
            return CancelBalance.Response.from(
                    transactionService.cancelBalance(request.getTransactionId(),
                            request.getAccountNumber(), request.getAmount())
            );
        } catch (AccountException e) {  // 거래 실패
            log.error("Failed to use balance.");

            transactionService.saveFailedCancelTransaction(    // 실패건만 저장한다.
                    request.getAccountNumber(),
                    request.getAmount()
            );
            throw e;
        }
    }


    @GetMapping("/transaction/{transactionId}")
    public QueryTransactionResponse queryTransaction(
            @PathVariable String transactionId){

        return QueryTransactionResponse.from(
                transactionService.queryTransaction(transactionId)
        );
    }
}
