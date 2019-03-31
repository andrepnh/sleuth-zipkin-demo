package com.github.andrepnh.tracing.shipment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.annotation.ContinueSpan;
import org.springframework.stereotype.Component;

@Component
public class ShippingManager {
  private static final Logger LOG = LoggerFactory.getLogger(Service.class);

  private final DelayInjection delayer;

  @Autowired
  public ShippingManager(DelayInjection delayer) {
    this.delayer = delayer;
  }

  @ContinueSpan(log = "defineShipmentSources")
  public void defineShipmentSources(int delay) {
    LOG.debug("Defining shipment sources...");
    delayer.delay(delay);
    LOG.debug("Done.");
  }
}
