package org.apache.coyote.http11.requestline;

import java.util.Arrays;

public enum HttpMethod {
  GET("GET"), POST("POST");

  private final String value;

  HttpMethod(final String value) {
    this.value = value;
  }

  public static HttpMethod from(final String method) {
    return Arrays.stream(values())
        .filter(it -> it.value.equals(method))
        .findAny()
        .orElseThrow();
  }
}
