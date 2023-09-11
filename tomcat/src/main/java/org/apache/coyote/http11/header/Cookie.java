package org.apache.coyote.http11.header;

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

  public String get(final String key) {
    return this.cookies.get(key);
  }

  public void put(final String key, final String value) {
    cookies.put(key, value);
  }
}
