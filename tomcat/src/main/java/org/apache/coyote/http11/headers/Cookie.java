package org.apache.coyote.http11.headers;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

  private final Map<String, String> cookies;

  public Cookie() {
    this.cookies = new HashMap<>();
  }

  public Cookie(final Map<String, String> cookies) {
    this.cookies = cookies;
  }

  public static Cookie from(final String cookieString) {
    final Map<String, String> cookies = new HashMap<>();
    if (cookieString == null || cookieString.isBlank()) {
      return new Cookie(cookies);
    }

    for (final String cookie : cookieString.split(";")) {
      final String[] tokens = cookie.split("=");
      cookies.put(tokens[0].trim(), tokens[1].trim());
    }
    return new Cookie(cookies);
  }

  public String get(final String key) {
    return this.cookies.get(key);
  }
}
