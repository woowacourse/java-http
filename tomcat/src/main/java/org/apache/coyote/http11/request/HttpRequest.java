package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.header.HeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.utils.IOUtils.readAsContentLength;
import static org.apache.coyote.http11.utils.IOUtils.readWhileEmptyLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.header.HeaderType;
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

  public static HttpRequest from(final BufferedReader br) throws IOException {
    final RequestLine requestLine = RequestLine.from(br.readLine());
    final HttpHeader header = HttpHeader.from(readWhileEmptyLine(br));
    final String body = readAsContentLength(br, header.getHeader(CONTENT_LENGTH));
    return new HttpRequest(requestLine, header, body);
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

  public boolean isUrlEndWith(final String url) {
    return requestLine.isUrlEndWith(url);
  }

  public String getUrl() {
    return requestLine.getUrl();
  }

  public String getHeader(final HeaderType key) {
    return header.getHeader(key);
  }
}
