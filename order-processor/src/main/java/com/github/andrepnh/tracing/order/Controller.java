package com.github.andrepnh.tracing.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-processor")
public class Controller {
  private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

  private final DelayInjection delayer;
  private final HttpClient client;

  @Autowired
  public Controller(DelayInjection delayer, HttpClient client) {
    this.delayer = delayer;
    this.client = client;
  }

  @GetMapping
  public void process() {
    LOG.info("Processing request...");
    delayer.delay();
    ResponseEntity<String> responseEntity = client.call("payment-gateway", String.class);
    LOG.info("Got {}: {}", responseEntity.getStatusCodeValue(), responseEntity.getBody());
    LOG.info("Done.");
  }
}