package com.github.andrepnh.tracing.payment;

import com.google.common.collect.ImmutableMap;
import io.github.resilience4j.retry.Retry;
import java.util.function.Supplier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HttpClient {
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
        .decorateSupplier(retry, () -> template.getForEntity(url, responseClass));
    return retry.executeSupplier(networkCall);
  }

}
