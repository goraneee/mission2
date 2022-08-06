package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.dto.TransactionDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.example.account.type.TransactionResultType.F;
import static com.example.account.type.TransactionResultType.S;
import static com.example.account.type.TransactionType.CANCEL;
import static com.example.account.type.TransactionType.USE;

@Slf4j
@Service
@RequiredArgsConstructor

public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;

/*
    사용자가 없는 경우, 계좌가 없는 경우, 사용자 아이디와 계좌 소유주가 다른 경우,
    계좌가 이미 해지 상태인 경우, 거래 금액이 잔액보다 큰 경우,
    거래 금액이 너무 적거나 큰 경우 실패 응답
*/

    @Transactional
    public TransactionDto useBalance(Long userId,
                                     String accountNumber,
                                     Long amount){
        AccountUser user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validationUseBalance(user, account, amount);

        // 성공한 경우
        account.useBalance(amount);

        return TransactionDto.fromEntity(saveAndGetTransaction(USE, S, account, amount));    // 성공한 경우

    }

    private  void validationUseBalance(AccountUser user,
                                       Account account,
                                       Long amount) {

        if (!Objects.equals(user.getId(), account.getAccountUser().getId())) {
            // 널에 대한 안전한 비교 방법이다.
            throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);
        }
        if (account.getAccountStatus() != AccountStatus.IN_USE) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (account.getBalance() < amount) {  // 잔고보다 많이 출금, 오류
           throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
    }

    @Transactional
    public void saveFailedUseTransaction(String accountNumber,  // 실패
                                         Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));  // 계좌 번호가 없는 경우

        saveAndGetTransaction(USE, F, account, amount);  // 실패한 경우
    }

    private Transaction saveAndGetTransaction(      // 규빈님코드
            TransactionType transactionType,
            TransactionResultType transactionResultType,
            Account account,
            Long amount) {
        return transactionRepository.save(
                Transaction.builder()
                        .transactionType(transactionType)
                        .transactionResultType(transactionResultType)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString()
                                .replace("-", ""))
                        .transactedAt(LocalDateTime.now())
                        .build()
        );
    }

    @Transactional
    public TransactionDto cancelBalance(String transactionId,
                                        String accountNumber,
                                        Long amount) {

        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateCancelBalance(transaction, account, amount);
        account.cancelBalance(amount);
        return  TransactionDto.fromEntity(
                saveAndGetTransaction(CANCEL, S, account, amount));
    }

    private void validateCancelBalance(Transaction transaction, Account account, Long amount) {

        if(!Objects.equals(transaction.getAccount().getId(), account.getId()) ){
            throw  new AccountException(ErrorCode.TRANSACTION_ACCOUNT_UN_MATCH);
        }

        if(!Objects.equals(transaction.getAmount(), amount) ){
            throw  new AccountException(ErrorCode.CANCEL_MUST_FULLY);
        }
        if(transaction.getTransactedAt().isBefore(LocalDateTime.now().minusYears(1))){
            throw new AccountException(ErrorCode.TOO_OLD_ORDER_TO_CANCEL);
        }
    }

    public void saveFailedCancelTransaction(String accountNumber, Long amount) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        saveAndGetTransaction(CANCEL, F, account, amount);

    }

    public TransactionDto queryTransaction(String transactionId) {

        return TransactionDto.fromEntity(
                    transactionRepository.findByTransactionId(transactionId)
                        .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND))
        );
    }


// 내 코드
//    private Transaction saveAndGetTransaction(TransactionResultType transactionResultType, Account account, Long amount) {
//        return transactionRepository.save(             // useBalance에서 S만 F로 바꾼다.
//                Transaction.builder()
//                        .transactionType(USE)
//                        .transactionResultType(transactionResultType)
//                        .account(account)
//                        .amount(amount)
//                        .balanceSnapshot(account.getBalance())
//                        .transactionId(UUID.randomUUID().toString().replace("-", ""))
//                        .transactedAt(LocalDateTime.now())
//                        .build()
//        );
//    }
}
