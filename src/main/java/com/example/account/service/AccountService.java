package com.example.account.service;
import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service    // 서비스 타입 빈으로 스프링에 자동 등록해주기 위해서 붙인다.
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;
    /**
     * 사용자가 있는지 조회
     * 계좌의 번호를 생성한다.
     * 계좌를 저장하고 그 정보를 넘긴다.
     */

    // 무조건 생성자에 들어가는 값
    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance){   // 테이블에 데이터를 저장한다.
        AccountUser accountUser = getAccountUser(userId);

        validateCreateAccount(accountUser);

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                .orElse("1000000000");   // 계좌가 없는 경우
        // 가장 마지막에 생성된 계좌번호 + 1 해서 넣어 준다.
        // 문자 => 숫자 => 문자

        return AccountDto.fromEntity(       // accountDto를 만들어서 리턴한다.
                accountRepository.save(
                        Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(initialBalance)
                        .accountNumber(newAccountNumber)
                        .registeredAt(LocalDateTime.now())
                        .build()
        ));
    }

    private void validateCreateAccount(AccountUser accountUser) {
        if(accountRepository.countByAccountUser(accountUser) == 10){
            throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_10);
        }
    }

    @Transactional
    public Account getAccount(Long id) {   // 계좌를 조회한다.
        if(id < 0){
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();
    }

    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {

        AccountUser accountUser = getAccountUser(userId);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

       validateDeleterAccount(accountUser, account);

       // 이제 계좌해지 가능 상태
       account.setAccountStatus(AccountStatus.UNREGISTERED);
       account.setUnregisteredAt(LocalDateTime.now());
       accountRepository.save(account); // 원칙 상 없는 게 맞다.
       return AccountDto.fromEntity(account);  // account로부터 dto를 만들어서 응답을 준다.
    }

    private void validateDeleterAccount(AccountUser accountUser, Account account) {

        if(!Objects.equals(accountUser.getId(), account.getAccountUser().getId())){
            throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);    // 고침
        }
        if(account.getAccountStatus() == AccountStatus.UNREGISTERED){
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if(account.getBalance() > 0){
            throw new AccountException(ErrorCode.BALANCE_NOT_EMPTY);
        }
    }

    @Transactional
    public List<AccountDto> getAccountByUserId(Long userId) {
        AccountUser accountUser = getAccountUser(userId);

        List<Account> accounts = accountRepository
                .findByAccountUser(accountUser);


        // List<Account>를 List<AccountDto>타입으로 바꿔준다.
        return accounts.stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());

    }

    private AccountUser getAccountUser(Long userId) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
        return accountUser;
    }
}
