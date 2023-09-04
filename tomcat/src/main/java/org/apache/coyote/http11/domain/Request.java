package org.apache.coyote.http11.domain;

public class Request {

  private final String method;
  private final String url;
  private final String queryString;
  private final String version;

  public Request(final String request) {
    final String[] tokens = request.split(" ");
    final int index = tokens[1].indexOf("?");
    this.method = tokens[0];
    this.url = index != -1 ? tokens[1].substring(0, index) : tokens[1];
    this.queryString = index != -1 ? tokens[1].substring(index + 1) : "";
    this.version = tokens[2];
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
