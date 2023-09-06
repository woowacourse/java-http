package org.apache.coyote.http11.domain;

import java.util.HashMap;
import java.util.Map;

public class HttpParam {

  private final Map<String, String> params;

  public HttpParam(final Map<String, String> params) {
    this.params = params;
  }

  public static HttpParam from(final String queryString) {
    final Map<String, String> params = new HashMap<>();
    if (queryString.isEmpty()) {
      return new HttpParam(params);
    }

    for (final String query : queryString.split("&")) {
      final String[] tokens = query.split("=");
      params.put(tokens[0], tokens[1]);
    }
    return new HttpParam(params);
  }

  public String get(final String key) {
    return this.params.get(key);
  }
}
