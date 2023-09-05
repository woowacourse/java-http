package org.apache.coyote.http11.domain;

public class Request {

  private final String method;
  private final String url;
  private final String queryString;
  private final String version;

  public Request(final String method, final String url, final String queryString,
      final String version) {
    this.method = method;
    this.url = url;
    this.queryString = queryString;
    this.version = version;
  }

  public static Request from(final String request) {
    final String[] tokens = request.split(" ");
    final int index = tokens[1].indexOf("?");
    final String method = tokens[0];
    final String url = index != -1 ? tokens[1].substring(0, index) : tokens[1];
    final String queryString = index != -1 ? tokens[1].substring(index + 1) : "";
    final String version = tokens[2];
    return new Request(method, url, queryString, version);
  }

  public String getMethod() {
    return this.method;
  }

  public String getUrl() {
    return this.url;
  }

  public String getQueryString() {
    return this.queryString;
  }

  public String getVersion() {
    return this.version;
  }
}
