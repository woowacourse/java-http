package org.apache.coyote.http11.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeader {

  private final Map<String, String> headers;

  public HttpHeader(final Map<String, String> headers) {
    this.headers = headers;
  }

  public static HttpHeader from(final BufferedReader br) throws IOException {
    final Map<String, String> headers = new HashMap<>();
    String line;
    while (!(line = br.readLine()).isEmpty()) {
      final String[] tokens = line.split(": ");
      headers.put(tokens[0].trim(), tokens[1].trim());
    }
    return new HttpHeader(headers);
  }

  public String get(final String key) {
    return this.headers.get(key);
  }
}
