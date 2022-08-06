package com.example.account.domain;

import com.example.account.type.AccountStatus;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Transaction extends BaseEntity {

    @Enumerated(EnumType.STRING)    // String 타입으로 저장한다.
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionResultType transactionResultType;

    @ManyToOne
    private Account account;    // 여러 거래가 한 계좌에 연결된다.
    private Long amount;
    private Long balanceSnapshot;

    private String transactionId;   // id는 pk라서 직접 쓰면 위험하다.
    private LocalDateTime transactedAt;

    //규빈님코드
/*
    @Id @GeneratedValue
    @Column(name = "t_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionResultType transactionResultType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private Long amount;
    private Long balanceSnapshot;
    private String transactionId;
    private LocalDateTime transactedAt;

//    public void cancelTransaction() {
//        this.transactionType = CANCEL;
//    }

 */

}
