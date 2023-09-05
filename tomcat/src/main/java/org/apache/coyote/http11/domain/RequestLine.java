package org.apache.coyote.http11.domain;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

  private final String method;
  private final String url;
  private final String version;
  private final Map<String, String> params;

  public RequestLine(final String method, final String url, final String version,
      final Map<String, String> params) {
    this.method = method;
    this.url = url;
    this.version = version;
    this.params = params;
  }

  public static RequestLine from(final String requestLine) {
    final String[] tokens = requestLine.split(" ");
    final String method = tokens[0];
    final String uri = tokens[1];
    final String url = parseUrl(uri);
    final Map<String, String> params = parseParams(uri);
    final String version = tokens[2];
    return new RequestLine(method, url, version, params);
  }

  private static Map<String, String> parseParams(final String uri) {
    final Map<String, String> params = new HashMap<>();
    final int index = uri.indexOf("?");
    if (index == -1) {
      return params;
    }
    for (final String queryString : uri.substring(index + 1).split("&")) {
      final String[] tokens = queryString.split("=", 2);
      params.put(tokens[0].trim(), tokens[1].trim());
    }
    return params;
  }

  private static String parseUrl(final String uri) {
    final int index = uri.indexOf("?");
    if (index == -1) {
      return uri;
    }
    return uri.substring(0, index);
  }

  public String getMethod() {
    return this.method;
  }

  public String getUrl() {
    return this.url;
  }

  public String getVersion() {
    return this.version;
  }

  public Map<String, String> getParams() {
    return this.params;
  }
}
