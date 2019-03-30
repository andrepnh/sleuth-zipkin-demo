package com.github.andrepnh.tracing.order;

import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ShipmentClient {
  private static final Logger LOG = LoggerFactory.getLogger(ShipmentClient.class);

  private final HttpClient client;

  public ShipmentClient(HttpClient client) {
    this.client = client;
  }

  public void ship() {
    ResponseEntity<Void> responseEntity = client.call("shipment", Void.class);
    LOG.info("Shipment {}: {}", responseEntity.getStatusCodeValue(), responseEntity.getBody());
  }
}
