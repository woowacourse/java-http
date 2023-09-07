package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.headers.HttpHeader;
import org.apache.coyote.http11.requestline.HttpMethod;
import org.apache.coyote.http11.requestline.RequestLine;

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
    final String body = readBody(br, header.getHeader(CONTENT_LENGTH));
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
    return URLDecoder.decode(new String(buffer), StandardCharsets.UTF_8);
  }

  private static List<String> readWhileEmptyLine(final BufferedReader br) throws IOException {
    final List<String> lines = new ArrayList<>();
    String line;
    while (!(line = br.readLine()).isEmpty()) {
      lines.add(line);
    }
    return lines;
  }

  public String getUrl() {
    return this.requestLine.getUrl();
  }

  public String getParam(final String key) {
    return this.requestLine.getParam(key);
  }

  public String getHeader(final String key) {
    return this.header.getHeader(key);
  }

  public String getCookie(final String key) {
    return this.header.getCookie(key);
  }

  public String getBody() {
    return this.body;
  }

  public HttpMethod getMethod() {
    return this.requestLine.getMethod();
  }
}
