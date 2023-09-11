package org.apache.coyote.http11.header;

import static org.apache.coyote.http11.header.HeaderType.COOKIE;
import static org.apache.coyote.http11.header.HeaderType.LOCATION;
import static org.apache.coyote.http11.header.HeaderType.SET_COOKIE;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeader {

  private final Map<HeaderType, String> headers;
  private final Cookie cookie;

  public HttpHeader() {
    this.headers = new EnumMap<>(HeaderType.class);
    this.cookie = new Cookie();
  }

  public HttpHeader(final Map<HeaderType, String> headers) {
    this.headers = headers;
    this.cookie = Cookie.from(headers.get(COOKIE));
  }

  public static HttpHeader from(final List<String> lines) {
    final Map<HeaderType, String> headers = new EnumMap<>(HeaderType.class);
    for (final String line : lines) {
      final String[] tokens = line.split(":", 2);
      headers.put(HeaderType.from(tokens[0].trim()), tokens[1].trim());
    }
    return new HttpHeader(headers);
  }

  public void setHeader(final HeaderType key, final String value) {
    this.headers.put(key, value);
  }

  public String getHeader(final HeaderType key) {
    return this.headers.get(key);
  }

  public String getCookie(final String key) {
    return this.cookie.get(key);
  }

  public String build() {
    return this.headers.entrySet().stream()
        .map(entry -> entry.getKey().getType() + ": " + entry.getValue())
        .collect(Collectors.joining(System.lineSeparator()));
  }

  public void setHeaderLocation(final String path) {
    this.headers.put(LOCATION, path);
  }

  public void setCookie(final String cookie) {
    this.headers.put(SET_COOKIE, cookie);
  }
}
