package org.apache.coyote.http11.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class HttpRequest {

  private final String method;
  private final String url;
  private final String version;
  private final HttpHeader header;
  private final HttpParam param;
  private final Cookie cookie;

  public HttpRequest(final String method, final String url, final String version,
      final HttpHeader header, final HttpParam param, final Cookie cookie) {
    this.method = method;
    this.url = url;
    this.version = version;
    this.header = header;
    this.param = param;
    this.cookie = cookie;
  }

  public static HttpRequest from(final BufferedReader br) throws IOException {
    final String requestLine = URLDecoder.decode(br.readLine(), StandardCharsets.UTF_8);
    final String[] tokens = requestLine.split(" ");
    final int index = tokens[1].indexOf("?");
    final String method = tokens[0];
    final String url = index != -1 ? tokens[1].substring(0, index) : tokens[1];
    final String queryString = index != -1 ? tokens[1].substring(index + 1) : "";
    final String version = tokens[2];
    final HttpHeader header = HttpHeader.from(br);
    final HttpParam param = HttpParam.from(queryString);
    final Cookie cookie = Cookie.from(header.get("cookie"));
    return new HttpRequest(method, url, version, header, param, cookie);
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

  public String getHeader(final String header) {
    return this.header.get(header);
  }

  public String getParam(final String key) {
    return this.param.get(key);
  }

  public String getCookie(final String key) {
    return this.cookie.get(key);
  }
}
