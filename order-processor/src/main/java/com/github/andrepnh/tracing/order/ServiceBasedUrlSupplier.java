package com.github.andrepnh.tracing.order;

import java.util.Map;
import java.util.stream.Collectors;

public class ServiceBasedUrlSupplier implements UrlSupplier {
  @Override
  public String get(String service) {
    return String.format("http://%s:%d/%s", service, getPort(service), service);
  }

  @Override
  public String get(String service, Map<String, String> params) {
    var queryParams = params.entrySet().stream()
        .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
        .collect(Collectors.joining("&"));
    return String.format("http://%s:%d/%s?%s", service, getPort(service), service, queryParams);
  }
}
