package com.example.account.service;


import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {
    private final RedissonClient redissonClient;

    public void lock(String accountNumber){
        RLock lock = redissonClient.getLock(getLockKey(accountNumber));// 락 이름이 샘플락, 자물쇠를 받는다.
        log.debug("Trying lock for accountNumber : " + accountNumber);

        try{
            boolean isLock = lock.tryLock(1, 15, TimeUnit.SECONDS);
            // 5초 동안 작업이 없으면 락이 풀린다.
            // 최대 1초 동안 기다리면서 자물쇠 찾는다.

            if(!isLock){
                log.error("============Lock acquisition failed ==============");
                throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
            }
        }catch (AccountException e){
            throw e;
        }catch (Exception e){
            log.error("Redis lock failed", e); // 다시 한 번 던진다.
        }
    }

    public void unlock(String accountNumber){
        log.debug("Unlock for accountNumber : {}", accountNumber);
        redissonClient.getLock(getLockKey(accountNumber)).unlock();
    }


    private String getLockKey(String accountNumber) {

        return "ACLK: " + accountNumber;
    }
}
