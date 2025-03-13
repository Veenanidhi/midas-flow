package com.jpmc.midascore.kafka;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionKafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(TransactionKafkaListener.class);
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionKafkaListener(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @KafkaListener(topics = "${kafka.topic.transactions}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void listen(Transaction transaction) {
        if (transaction == null) {
            logger.warn("Received a null transaction object.");
            return;
        }

        try {
            UserRecord sender = userRepository.findById(transaction.getSenderId());
            UserRecord recipient = userRepository.findById(transaction.getRecipientId());

            if (sender == null || recipient == null) {
                logger.warn("Invalid sender or recipient. Transaction discarded.");
                return;
            }

            if (sender.getBalance() < transaction.getAmount()) {
                logger.warn("Insufficient balance for sender. Transaction discarded.");
                return;
            }

            // Deduct from sender and add to recipient
            sender.updateBalance(-transaction.getAmount());
            recipient.updateBalance(transaction.getAmount());

            // Save updated balances
            userRepository.save(sender);
            userRepository.save(recipient);

            // Save transaction record
            TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount());
            transactionRepository.save(transactionRecord);

            logger.info("Transaction successfully processed: {}", transaction);

        } catch (Exception e) {
            logger.error("Error processing transaction: {}", e.getMessage(), e);
        }
    }
}
