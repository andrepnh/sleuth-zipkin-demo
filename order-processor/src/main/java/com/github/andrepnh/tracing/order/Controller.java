package com.github.andrepnh.tracing.order;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-processor")
public class Controller {
  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayClient.class);

  private final DelayInjection delayer;
  private final PaymentGatewayClient paymentGatewayClient;
  private final InventoryReservationClient inventoryReservationClient;
  private final ShipmentClient shipmentClient;

  @Autowired
  public Controller(DelayInjection delayer,
      PaymentGatewayClient paymentGatewayClient,
      InventoryReservationClient inventoryReservationClient,
      ShipmentClient shipmentClient) {
    this.delayer = delayer;
    this.paymentGatewayClient = paymentGatewayClient;
    this.inventoryReservationClient = inventoryReservationClient;
    this.shipmentClient = shipmentClient;
  }

  @GetMapping
  public void process() throws ExecutionException, InterruptedException {
    LOG.info("Processing request...");
    delayer.delay();
    CompletableFuture<Boolean> paymentSuccessful = paymentGatewayClient.processPayment();
    CompletableFuture<Void> inventoryReserved = inventoryReservationClient.reserveInventory();
    paymentSuccessful
        .thenCombine(inventoryReserved, (success, nil) -> success
            ? shipmentClient.ship()
            : inventoryReservationClient.cancelReservation())
        .get();
    LOG.info("Done.");
  }
}