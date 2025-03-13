package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
public class IncentiveService {

    private final RestTemplate restTemplate;

    public IncentiveService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getIncentiveAmount(Transaction transaction) {
        String url = "http://localhost:8080/incentive";

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Wrap transaction in an HTTP request
        HttpEntity<Transaction> request = new HttpEntity<>(transaction, headers);

        // Make API call
        ResponseEntity<Incentive> response = restTemplate.postForEntity(url, request, Incentive.class);

        // Return the incentive amount
        return (response.getBody() != null) ? response.getBody().getAmount() : 0.0;
    }
}
