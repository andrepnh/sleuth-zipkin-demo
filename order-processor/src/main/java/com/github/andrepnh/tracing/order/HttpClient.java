package com.github.andrepnh.tracing.order;

import static java.util.Objects.requireNonNull;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HttpClient {
  private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);

  private final RestTemplate template;
  private final UrlSupplier urlSupplier;
  private final boolean retry;
  private final RetryConfig retryConfig;

  public HttpClient(RestTemplate template, UrlSupplier urlSupplier, boolean retry,
      int retryIntervalMs) {
    this.template = template;
    this.urlSupplier = urlSupplier;
    this.retry = retry;
    retryConfig = RetryConfig.custom()
        .waitDuration(Duration.ofMillis(retryIntervalMs))
        .build();
    LOG.info("Using a retry interval of {}ms", retryIntervalMs);
  }

  public <T> ResponseEntity<T> call(String service, Class<T> responseClass) {
    return doCall(service, responseClass, null);
  }

  public <T> ResponseEntity<T> call(
      String service, Class<T> responseClass, Map<String, String> params) {
    return doCall(service, responseClass, requireNonNull(params));
  }

  private <T> ResponseEntity<T> doCall(
      String service, Class<T> responseClass, Map<String, String> params) {
    Supplier<ResponseEntity<T>> networkCall = () -> {
      var url = params == null ? urlSupplier.get(service) : urlSupplier.get(service, params);
      LOG.debug("Calling {} (with retry? {})", url, retry);
      return template.getForEntity(url, responseClass);
    };
    if (retry) {
      var retrier = Retry.of(service, retryConfig);
      return retrier.executeSupplier(Retry.decorateSupplier(retrier, networkCall));
    }
    return networkCall.get();
  }

}
