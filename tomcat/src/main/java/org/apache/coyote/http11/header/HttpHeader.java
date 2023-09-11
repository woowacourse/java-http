package org.apache.coyote.http11.header;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeader {

  private static final String HEADER_DELIMITER = ": ";
  private final Map<HeaderType, String> header;

  public HttpHeader() {
    this.header = new EnumMap<>(HeaderType.class);
  }

  public HttpHeader(final Map<HeaderType, String> header) {
    this.header = header;
  }

  public static HttpHeader from(final List<String> lines) {
    final Map<HeaderType, String> headers = new EnumMap<>(HeaderType.class);

    lines.forEach(line -> {
      final String[] tokens = line.split(HEADER_DELIMITER, 2);
      headers.put(HeaderType.from(tokens[0].trim()), tokens[1].trim());
    });

    return new HttpHeader(headers);
  }

  public void setHeader(final HeaderType key, final String value) {
    header.put(key, value);
  }

  public String getHeader(final HeaderType key) {
    return header.get(key);
  }

  public String build() {
    return header.entrySet().stream()
        .map(entry -> entry.getKey().getType() + HEADER_DELIMITER + entry.getValue())
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
