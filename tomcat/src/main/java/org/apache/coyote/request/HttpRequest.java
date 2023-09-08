package org.apache.coyote.request;

import java.io.IOException;
import java.util.UUID;
import org.apache.catalina.Manager;

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
      final Cookie cookie,
      final Manager sessionManager
  ) {
    this.httpRequestLine = httpRequestLine;
    this.queryString = queryString;
    this.requestBody = requestBody;
    this.cookie = cookie;
    initializeSession(sessionManager);
  }

  private void initializeSession(final Manager sessionManager) {
    cookie.getJSessionId()
        .ifPresentOrElse(
            id -> {
              try {
                addSession(sessionManager.findSession(id));
              } catch (IOException e) {
                initializeSession(sessionManager);
              }
            },
            () -> {
              final Session session = new Session(UUID.randomUUID().toString());
              sessionManager.add(session);
              addSession(session);
            }
        );
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

  public String getUri() {
    return httpRequestLine.getUri();
  }

  public RequestBody getRequestBody() {
    return requestBody;
  }

  public Cookie getCookie() {
    return cookie;
  }

  public Session getSession() {
    return session;
  }

  public void addSession(final Session session) {
    this.session = session;
  }
}
