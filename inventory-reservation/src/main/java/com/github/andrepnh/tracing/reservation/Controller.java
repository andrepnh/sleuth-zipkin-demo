package com.github.andrepnh.tracing.reservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory-reservation")
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

    LOG.info("Done.");
  }
}
