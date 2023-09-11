package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ParseUtils {

  private ParseUtils() {
  }

  public static String parseContentType(final String accept) {
    if (accept == null) {
      return "text/html";
    }
    return accept.split(",")[0];
  }

  public static Map<String, String> parseParam(final String queryString) {
    final Map<String, String> params = new HashMap<>();
    for (final String query : URLDecoder.decode(queryString, StandardCharsets.UTF_8).split("&")) {
      final String[] tokens = query.split("=", 2);
      params.put(tokens[0], tokens[1]);
    }
    return params;
  }
}
