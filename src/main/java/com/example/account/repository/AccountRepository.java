package com.example.account.repository;
import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findFirstByOrderByIdDesc();
    //형식에 맟줘 이름을 지으면 형태에 맞춰 쿼리 짜준다.

    Integer countByAccountUser(AccountUser accountUser);

    // AccountNumber을 통해 검색해서 결과를 준다.?
    Optional<Account> findByAccountNumber(String AccountNumber);

    List<Account> findByAccountUser(AccountUser accountUser);
}
