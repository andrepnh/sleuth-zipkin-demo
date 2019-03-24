package com.github.andrepnh.tracing.order;

import com.google.common.collect.ImmutableMap;
import io.github.resilience4j.retry.Retry;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HttpClient {
  private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);

  private static final ImmutableMap<String, Integer> PORTS_PER_SERVICE = ImmutableMap
      .<String, Integer>builder()
      .put("order-processor", 8000)
      .put("payment-gateway", 8001)
      .put("inventory-reservation", 8002)
      .put("shipment", 8003)
      .build();

  private final RestTemplate template;

  private final UrlSupplier urlSupplier;

  public HttpClient(RestTemplate template, UrlSupplier urlSupplier) {
    this.template = template;
    this.urlSupplier = urlSupplier;
  }

  public <T> ResponseEntity<T> call(String service, Class<T> responseClass) {
    var retry = Retry.ofDefaults(service);
    Supplier<ResponseEntity<T>> networkCall = Retry
        .decorateSupplier(retry, () -> {
          var url = urlSupplier.get(service);
          LOG.debug("Calling {}", url);
          return template.getForEntity(url, responseClass);
        });
    return retry.executeSupplier(networkCall);
  }

  public <T> ResponseEntity<T> call(
      String service, Class<T> responseClass, Map<String, String> params) {
    var retry = Retry.ofDefaults(service);
    Supplier<ResponseEntity<T>> networkCall = Retry
        .decorateSupplier(retry, () -> {
          var url = urlSupplier.get(service, params);
          LOG.debug("Calling {}", url);
          return template.getForEntity(url, responseClass);
        });
    return retry.executeSupplier(networkCall);
  }

}
