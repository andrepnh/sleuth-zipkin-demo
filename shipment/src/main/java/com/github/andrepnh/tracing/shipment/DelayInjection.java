package com.github.andrepnh.tracing.shipment;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DelayInjection {
  private static final Logger LOG = LoggerFactory.getLogger(DelayInjection.class);
  private static final Random RNG = new Random();

  private final int minDelayMs;
  private final int maxDelayMs;

  @Autowired
  public DelayInjection(
      @Value("${min.delay.ms}") int minDelayMs,
      @Value("${read.timeout.ms}") int readTimeoutMs,
      @Value("${failure.rate.to.simulate}") double targetFailureRate) {
    // We have minDelayMs, readTimeoutMs and a targetFailureRate. We want to find the maxDelayMs
    // such that java.util.Random.nextInt(minDelayMs, maxDelayMs) has a targetFailureRate chance of
    // pulling any delay that would cause a timeout in the client application.
    // Since Random.nextInt gives roughly the same chance of coming up with any integer within 
    // bounds, we have that:
    // maxDelayMs - minDelayMs = possibleDelaysQty
    // 1 / possibleDelaysQty = chanceOfAnyDelay
    // maxDelayMs - readTimeoutMs = possibleTimeoutTriggeringDelays
    // chanceOfAnyDelay * possibleTimeoutTriggeringDelays = targetFailureRate
    //
    // Moving things around:
    // (1 / possibleDelaysQty) * (maxDelayMs - readTimeoutMs) = targetFailureRate
    // (maxDelayMs - readTimeoutMs) / possibleDelaysQty = targetFailureRate
    // (maxDelayMs - readTimeoutMs) / (maxDelayMs - minDelayMs) = targetFailureRate
    // targetFailureRate * maxDelayMs - targetFailureRate * minDelayMs = maxDelayMs - readTimeoutMs
    // targetFailureRate * maxDelayMs - maxDelayMs = targetFailureRate * minDelayMs - readTimeoutMs
    // maxDelayMs * (targetFailureRate - 1) = targetFailureRate * minDelayMs - readTimeoutMs
    // maxDelayMs = (targetFailureRate * minDelayMs - readTimeoutMs) / (targetFailureRate - 1)
    this.maxDelayMs = (int) Math.round(
        (targetFailureRate * minDelayMs - readTimeoutMs) / (targetFailureRate - 1));
    this.minDelayMs = minDelayMs;
    LOG.debug("Delaying anything between {} (inclusive) and {} (exclusive). "
            + "Based on timeout:{} and failure rate:{}",
        minDelayMs, maxDelayMs, readTimeoutMs, targetFailureRate);
  }

  public void delay() {
    delay(RNG.nextInt(maxDelayMs - minDelayMs) + minDelayMs);
  }

  public void delay(int delay) {
    LOG.debug("Delaying for {}ms", delay);
    try {
      TimeUnit.MILLISECONDS.sleep(delay);
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }
  }

  public int nextDelayMs() {
    return RNG.nextInt(maxDelayMs - minDelayMs) + minDelayMs;
  }
}
