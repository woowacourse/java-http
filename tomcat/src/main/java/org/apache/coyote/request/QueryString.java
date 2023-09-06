package org.apache.coyote.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueryString {

  private final Map<String, String> value;

  private QueryString(final Map<String, String> value) {
    this.value = value;
  }

  public static QueryString from(final String path) {
    final Map<String, String> queryStringMap = new HashMap<>();
    final int queryStringIndex = path.indexOf("?");

    if (queryStringIndex < 0) {
      return new QueryString(Collections.emptyMap());
    }

    final String[] queryStrings = path.substring(queryStringIndex + 1)
        .split("&");

    for (final String queryString : queryStrings) {
      final String[] queryStringKeyValue = queryString.split("=");

      queryStringMap.put(queryStringKeyValue[0], queryStringKeyValue[1]);
    }

    return new QueryString(queryStringMap);
  }

  public Map<String, String> getValue() {
    return value;
  }
}
