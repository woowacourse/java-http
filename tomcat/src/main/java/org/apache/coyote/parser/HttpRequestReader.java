package org.apache.coyote.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.request.Cookie;
import org.apache.coyote.request.HttpRequestLine;
import org.apache.coyote.request.QueryString;
import org.apache.coyote.request.RequestBody;

public class HttpRequestReader {

  public static HttpRequestLine parseHttpRequestLine(final List<String> headers) {
    final String requestLine = headers.get(0);

    final String[] element = requestLine.split(" ");

    return new HttpRequestLine(element[0], element[1]);
  }

  public static RequestBody parseRequestBody(
      final List<String> headers,
      final BufferedReader bufferedReader
  ) throws IOException {
    int contentLength = 0;
    for (final String header : headers) {
      if (header.startsWith("Content-Length: ")) {
        contentLength = Integer.parseInt(header.split(":")[1].trim());
        break;
      }
    }

    final StringBuilder stringBuilder = new StringBuilder();

    if (contentLength > 0) {
      char[] bodyChars = new char[contentLength];
      bufferedReader.read(bodyChars, 0, contentLength);
      stringBuilder.append(bodyChars);
    }

    return RequestBody.from(stringBuilder.toString());
  }

  public static QueryString parseQueryString(final List<String> headers) {
    final String requestLine = headers.get(0);

    final String[] element = requestLine.split(" ");

    return QueryString.from(element[1]);
  }

  public static Cookie parseCookie(final List<String> headers) {
    final StringBuilder stringBuilder = new StringBuilder();
    for (final String header : headers) {
      if (header.startsWith("Cookie: ")) {
        stringBuilder.append(header.split(":")[1].trim());
        break;
      }
    }

    final String cookieData = stringBuilder.toString();

    return Cookie.from(cookieData);
  }
}
