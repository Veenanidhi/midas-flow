package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Balance;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BalanceService {

    // Simulating a user balance storage
    private final Map<Long, Double> userBalances = new ConcurrentHashMap<>();

    public Balance getBalance(Long userId) {
        double balanceAmount = userBalances.getOrDefault(userId, 0.0);
        return new Balance((float) balanceAmount);
    }

    // Optional: Method to update balance (if needed for other functionalities)
    public void updateBalance(Long userId, double amount) {
        userBalances.put(userId, amount);
    }
}
