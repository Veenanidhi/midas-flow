package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class UserRecordService {

    @Autowired
    private UserRepository userRecordRepository;

    @Autowired
    private RestTemplate restTemplate;

    public void processTransaction(Transaction transaction) {
        // ✅ 1. Send transaction to Incentives API
        Incentive incentive = restTemplate.postForObject(
                "http://localhost:8080/incentive",
                transaction,
                Incentive.class
        );

        // ✅ 2. Get the incentive amount
        float incentiveAmount = (incentive != null) ? (float) incentive.getAmount() : 0;

        // ✅ 3. Find the recipient
        UserRecord recipient = Optional.ofNullable(userRecordRepository.findById(transaction.getRecipientId()))
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        // ✅ 4. Update recipient balance
        recipient.updateBalance(transaction.getAmount() + incentiveAmount);
        userRecordRepository.save(recipient);
    }
}
