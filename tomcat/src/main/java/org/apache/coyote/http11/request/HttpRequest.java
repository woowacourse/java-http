package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.header.HeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.utils.IOUtils.readAsContentLength;
import static org.apache.coyote.http11.utils.IOUtils.readWhileEmptyLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.header.HeaderType;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.requestline.HttpMethod;
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

  public String getUrl() {
    return this.requestLine.getUrl();
  }

  public String getParam(final String key) {
    return this.requestLine.getParam(key);
  }

  public String getHeader(final HeaderType key) {
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

  public void addSession(final Session session) {
    this.sessionManager.add(session);
  }

  public Session getSession(final String id) {
    return this.sessionManager.findSession(id);
  }

  public boolean isGetMethod() {
    return this.requestLine.isGetMethod();
  }

  public boolean isPostMethod() {
    return this.requestLine.isPostMethod();
  }

  public boolean isSameUrl(final String url) {
    return this.requestLine.isSameUrl(url);
  }

  public boolean isEndWith(final String... filenameExtensions) {
    return Arrays.stream(filenameExtensions)
        .anyMatch(this.requestLine::isEndWith);
  }
}
