package com.github.andrepnh.tracing.order;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Objects;

public interface UrlSupplier {
  ImmutableMap<String, Integer> PORTS_PER_SERVICE = ImmutableMap
      .<String, Integer>builder()
      .put("order-processor", 8000)
      .put("payment-gateway", 8001)
      .put("inventory-reservation", 8002)
      .put("shipment", 8003)
      .build();

  String get(String service);

  String get(String service, Map<String, String> queryParams);

  default int getPort(String service) {
    return Objects.requireNonNull(PORTS_PER_SERVICE.get(service));
  }
}
