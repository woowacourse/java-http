package org.apache.coyote.response;

import java.util.Map;
import java.util.stream.Collectors;

public class ResponseCookie {

  private final Map<String, String> value;

  public ResponseCookie(final Map<String, String> value) {
    this.value = value;
  }

  public String read() {
    return value.entrySet()
        .stream()
        .map(it -> it.getKey() + "=" + it.getValue())
        .collect(Collectors.joining("; "));
  }
}
