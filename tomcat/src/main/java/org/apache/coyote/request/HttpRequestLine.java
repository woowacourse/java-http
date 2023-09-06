package org.apache.coyote.request;

public class HttpRequestLine {

  private final String httpMethod;
  private final String uri;

  public HttpRequestLine(final String httpMethod, final String uri) {
    this.httpMethod = httpMethod;
    this.uri = uri;
  }

  public boolean isGetMethod() {
    return httpMethod.equals("GET");
  }

  public boolean isPostMethod() {
    return httpMethod.equals("POST");
  }

  public boolean isStartWith(final String path) {
    return uri.startsWith(path);
  }

  public boolean isSameUri(final String uri) {
    return this.uri.equals(uri);
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public String getUri() {
    return uri;
  }
}
