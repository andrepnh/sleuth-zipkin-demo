package com.github.andrepnh.tracing.order;

import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayClient {
  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayClient.class);

  private final HttpClient client;

  public PaymentGatewayClient(HttpClient client) {
    this.client = client;
  }

  @Async
  public CompletableFuture<Boolean> processPayment() {
    ResponseEntity<Boolean> responseEntity = client.call("payment-gateway", Boolean.class);
    LOG.info("Got {}: {}", responseEntity.getStatusCodeValue(), responseEntity.getBody());
    return CompletableFuture.completedFuture(responseEntity.getBody());
  }

  public Boolean processPaymentSync() {
    ResponseEntity<Boolean> responseEntity = client.call("payment-gateway", Boolean.class);
    LOG.info("Got {}: {}", responseEntity.getStatusCodeValue(), responseEntity.getBody());
    return responseEntity.getBody();
  }
}
