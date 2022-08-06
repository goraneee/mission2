package com.example.account.domain;
import com.example.account.exception.AccountException;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Account  extends BaseEntity {

    @ManyToOne
    private AccountUser accountUser;
    private String accountNumber;


    // String을 붙여줘야 0, 1, 2, .. 과 같은 숫자가 아닌
    // AccountStatus에 있는 문자대로 DB에 저장
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;    // 계좌 상태
    private Long balance;                   // 잔액

    private LocalDateTime registeredAt;
    private LocalDateTime unregisteredAt;


    public void useBalance(Long amount){    // 잔액 비교
        if(amount > balance){
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
        balance -= amount;
    }

    public void cancelBalance(Long amount){    // 잔액 비교
        if(amount < 0){
            throw new AccountException(ErrorCode.INVALID_REQUEST);
        }
        balance += amount;  // 틀렸던 부분
    }
}
