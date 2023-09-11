package org.apache.coyote.http11.request;

import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.requestline.RequestLine;

public class HttpRequest {

  private final SessionManager sessionManager = new SessionManager();
  private final RequestLine requestLine;
  private final HttpHeader header;
  private final String body;

  public HttpRequest(final RequestLine requestLine, final HttpHeader header, final String body) {
    this.requestLine = requestLine;
    this.header = header;
    this.body = body;
  }

  public String getCookie(final String key) {
    return header.getCookie(key);
  }

  public Optional<Session> findSession(final String sessionId) {
    final Session session = sessionManager.findSession(sessionId);
    return session == null ? Optional.empty() : Optional.of(session);
  }

  public String getBody() {
    return body;
  }

  public void addSession(final Session session) {
    sessionManager.add(session);
  }

  public boolean isGetMethod() {
    return requestLine.isGetMethod();
  }

  public boolean isPostMethod() {
    return requestLine.isPostMethod();
  }
}
