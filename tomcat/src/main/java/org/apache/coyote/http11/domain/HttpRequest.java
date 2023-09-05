package org.apache.coyote.http11.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest {

  private static final String CONTENT_LENGTH = "Content-Length";

  private final RequestLine requestLine;
  private final HttpHeader header;
  private final String body;

  public HttpRequest(final RequestLine requestLine, final HttpHeader header, final String body) {
    this.requestLine = requestLine;
    this.header = header;
    this.body = body;
  }

  public static HttpRequest from(final BufferedReader br) throws IOException {
    final RequestLine requestLine = RequestLine.from(br.readLine());
    final HttpHeader header = HttpHeader.from(readWhileEmptyLine(br));
    final String body = readBody(br, header.get(CONTENT_LENGTH));
    return new HttpRequest(requestLine, header, body);
  }

  private static String readBody(
      final BufferedReader br,
      final String contentLength
  ) throws IOException {
    if (contentLength == null) {
      return "";
    }
    final int length = Integer.parseInt(contentLength);
    final char[] buffer = new char[length];
    br.read(buffer);
    return new String(buffer);
  }

  private static List<String> readWhileEmptyLine(final BufferedReader br) throws IOException {
    final List<String> lines = new ArrayList<>();
    String line;
    while (!(line = br.readLine()).isEmpty()) {
      lines.add(line);
    }
    return lines;
  }

  public RequestLine getRequestLine() {
    return this.requestLine;
  }

  public HttpHeader getHeader() {
    return this.header;
  }

  public String getBody() {
    return this.body;
  }
}
