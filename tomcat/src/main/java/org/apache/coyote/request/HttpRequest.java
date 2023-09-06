package org.apache.coyote.request;

import java.util.UUID;

public class HttpRequest {

  private final HttpRequestLine httpRequestLine;
  private final QueryString queryString;
  private final RequestBody requestBody;
  private final Cookie cookie;
  private Session session;

  public HttpRequest(
      final HttpRequestLine httpRequestLine,
      final QueryString queryString,
      final RequestBody requestBody,
      final Cookie cookie
  ) {
    this.httpRequestLine = httpRequestLine;
    this.queryString = queryString;
    this.requestBody = requestBody;
    this.cookie = cookie;
  }

  public boolean isPostMethod() {
    return httpRequestLine.isPostMethod();
  }

  public boolean isGetMethod() {
    return httpRequestLine.isGetMethod();
  }

  public boolean isStartWith(final String path) {
    return httpRequestLine.isStartWith(path);
  }

  public boolean isSameUri(final String uri) {
    return httpRequestLine.isSameUri(uri);
  }

  public void addCookie(final String value) {
    cookie.putJSessionId(value);
  }

  public boolean hasCookie() {
    return cookie.isNotEmpty();
  }

  public Session getSession() {
    if (session == null) {
      session = new Session(UUID.randomUUID().toString());
      return session;
    }
    return session;
  }

  public HttpRequestLine getHttpRequestLine() {
    return httpRequestLine;
  }

  public String getHttpMethod() {
    return httpRequestLine.getHttpMethod();
  }

  public String getUri() {
    return httpRequestLine.getUri();
  }

  public QueryString getQueryString() {
    return queryString;
  }

  public RequestBody getRequestBody() {
    return requestBody;
  }

  public Cookie getCookie() {
    return cookie;
  }
}
