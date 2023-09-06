package org.apache.coyote.request;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {

  private final Map<String, String> value;

  private Cookie(final Map<String, String> value) {
    this.value = value;
  }

  public static Cookie from(final String cookieData) {
    if (cookieData.isEmpty()) {
      return new Cookie(new HashMap<>());
    }

    final String[] split = cookieData.split(";");
    final Map<String, String> cookieMap = new HashMap<>();

    for (final String s : split) {
      final String[] split1 = s.split("=");

      cookieMap.put(split[0], split1[1]);
    }

    return new Cookie(cookieMap);
  }

  public void putJSessionId(final String value) {
    this.value.put("JSESSIONID", value);
  }

  public boolean isNotEmpty() {
    return !value.isEmpty();
  }

  @Override
  public String toString() {
    return value.entrySet()
        .stream()
        .map(it -> it.getKey() + "=" + it.getValue())
        .collect(Collectors.joining("; "));
  }
}
