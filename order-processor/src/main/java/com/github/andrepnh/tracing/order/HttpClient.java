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

  public HttpClient(RestTemplate template) {
    this.template = template;
  }

  public <T> ResponseEntity<T> call(String service, Class<T> responseClass) {
    var retry = Retry.ofDefaults(service);
    var url = String.format("http://localhost:%d/%s", PORTS_PER_SERVICE.get(service), service);
    Supplier<ResponseEntity<T>> networkCall = Retry
        .decorateSupplier(retry, () -> {
          LOG.debug("Calling {}", url);
          return template.getForEntity(url, responseClass);
        });
    return retry.executeSupplier(networkCall);
  }

  public <T> ResponseEntity<T> call(
      String service, Class<T> responseClass, Map<String, String> params) {
    var retry = Retry.ofDefaults(service);
    var queryParams = params.entrySet().stream()
        .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
        .collect(Collectors.joining("&"));
    var url = String.format("http://localhost:%d/%s?%s",
        PORTS_PER_SERVICE.get(service), service, queryParams);
    Supplier<ResponseEntity<T>> networkCall = Retry
        .decorateSupplier(retry, () -> {
          LOG.debug("Calling {}", url);
          return template.getForEntity(url, responseClass);
        });
    return retry.executeSupplier(networkCall);
  }

}
