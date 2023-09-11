package org.apache.coyote.http11.header;

import java.util.EnumMap;
import java.util.Map;

public class HttpHeader {

  private final Map<HeaderType, String> headers;
  private final Cookie cookie;

  public HttpHeader() {
    this.headers = new EnumMap<>(HeaderType.class);
    this.cookie = new Cookie();
  }

  public String getCookie(final String key) {
    final String value = cookie.get(key);
    return value == null ? "" : value;
  }

  public void setHeader(final HeaderType key, final String value) {
    headers.put(key, value);
  }

  public void putCookie(final String key, final String value) {
    cookie.put(key, value);
  }
}
