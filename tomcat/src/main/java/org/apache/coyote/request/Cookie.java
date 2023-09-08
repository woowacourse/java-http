package org.apache.coyote.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cookie {

  private static final String JSESSIONID = "JSESSIONID";

  private final Map<String, String> value;

  private Cookie(final Map<String, String> value) {
    this.value = value;
  }

  public static Cookie from(final String cookieData) {
    if (cookieData.isEmpty()) {
      return new Cookie(new HashMap<>());
    }

    final String[] parsedCookieData = cookieData.split(";");
    final Map<String, String> cookieMap = new HashMap<>();

    for (final String data : parsedCookieData) {
      final String[] splitData = data.split("=");

      cookieMap.put(splitData[0], splitData[1]);
    }

    return new Cookie(cookieMap);
  }

  public void putJSessionId(final String value) {
    this.value.put(JSESSIONID, value);
  }

  public Optional<String> getJSessionId() {
    return Optional.ofNullable(value.get(JSESSIONID));
  }

  public Map<String, String> getValue() {
    return value;
  }

  public String read() {
    return value.entrySet()
        .stream()
        .map(it -> it.getKey() + "=" + it.getValue())
        .collect(Collectors.joining("; "));
  }
}
