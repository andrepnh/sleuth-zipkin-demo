package com.github.andrepnh.tracing.shipment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.annotation.NewSpan;

@org.springframework.stereotype.Service
public class Service {
  private static final Logger LOG = LoggerFactory.getLogger(Service.class);

  private final DelayInjection delayer;
  private final ShippingManager shippingManager;

  @Autowired
  public Service(DelayInjection delayer,
      ShippingManager shippingManager) {
    this.delayer = delayer;
    this.shippingManager = shippingManager;
  }

  @NewSpan
  public void processShipment(int delay) {
    LOG.info("Processing shipment...");
    shippingManager.defineShipmentSources(delay / 2);
    delayer.delay(delay / 2);
    LOG.info("Done.");
  }
}
