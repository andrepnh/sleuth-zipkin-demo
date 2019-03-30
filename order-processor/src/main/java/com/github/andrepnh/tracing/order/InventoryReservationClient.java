package com.github.andrepnh.tracing.order;

import com.google.common.collect.ImmutableMap;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class InventoryReservationClient {
  private static final Logger LOG = LoggerFactory.getLogger(InventoryReservationClient.class);

  private final HttpClient client;

  public InventoryReservationClient(HttpClient client) {
    this.client = client;
  }

  @Async
  public CompletableFuture<Void> reserveInventory() {
    ResponseEntity<Void> responseEntity = client.call("inventory-reservation", Void.class);
    LOG.info("Reserve inventory {}: {}",
        responseEntity.getStatusCodeValue(), responseEntity.getBody());
    return CompletableFuture.completedFuture(responseEntity.getBody());
  }

  public Void reserveInventorySync() {
    ResponseEntity<Void> responseEntity = client.call("inventory-reservation", Void.class);
    LOG.info("Reserve inventory {}: {}",
        responseEntity.getStatusCodeValue(), responseEntity.getBody());
    return null;
  }

  public void cancelReservation() {
    var queryParams = ImmutableMap.of("cancelReservation", "true");
    ResponseEntity<Void> responseEntity = client
        .call("inventory-reservation", Void.class, queryParams);
    LOG.info("Cancel reservation {}: {}",
        responseEntity.getStatusCodeValue(), responseEntity.getBody());
  }
}
