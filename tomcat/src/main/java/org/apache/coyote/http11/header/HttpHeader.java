package org.apache.coyote.http11.header;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeader {

  private final Map<String, String> headers;
  private final Cookie cookie;

  public HttpHeader() {
    this.headers = new HashMap<>();
    this.cookie = new Cookie();
  }

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

  public void setHeader(final String key, final String value) {
    this.headers.put(key, value);
  }

  public String getHeader(final String key) {
    return this.headers.get(key);
  }

  public String getCookie(final String key) {
    return this.cookie.get(key);
  }

  public String build() {
    return this.headers.entrySet().stream()
        .map(entry -> entry.getKey() + ": " + entry.getValue())
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
