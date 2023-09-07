package org.apache.coyote.http11.headers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeader {

  private final Map<String, String> headers;
  private final Cookie cookie;

  public HttpHeader(final Map<String, String> headers) {
    this.headers = headers;
    this.cookie = Cookie.from(headers.get("Cookie"));
  }

  public static HttpHeader from(final List<String> lines) {
    final Map<String, String> headers = new HashMap<>();
    for (final String line : lines) {
      final String[] tokens = line.split(":", 2);
      headers.put(tokens[0].trim(), tokens[1].trim());
    }
    return new HttpHeader(headers);
  }

  public String getHeader(final String key) {
    return this.headers.get(key);
  }

  public String getCookie(final String key) {
    return this.cookie.get(key);
  }
}
