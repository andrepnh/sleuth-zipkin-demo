package com.github.andrepnh.tracing.payment;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfiguration {
  @Bean
  public HttpClient httpClient(
      @Value("${read.timeout.ms}") int readTimeoutMs,
      RestTemplateBuilder builder) {
    var restTemplate = builder.setReadTimeout(Duration.ofMillis(readTimeoutMs)).build();
    return new HttpClient(restTemplate);
  }
}
