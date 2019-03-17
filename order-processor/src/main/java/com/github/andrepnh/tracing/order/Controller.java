package com.github.andrepnh.tracing.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-processor")
public class Controller {
  private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

  @GetMapping
  public void process() {
    LOG.info("Processing request...");

    LOG.info("Done.");
  }
}
