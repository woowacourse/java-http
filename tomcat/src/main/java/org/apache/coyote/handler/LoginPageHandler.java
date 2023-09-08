package org.apache.coyote.handler;

import java.io.IOException;
import org.apache.coyote.detector.FileDetector;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.Charset;
import org.apache.coyote.response.ContentType;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpResponseHeader;
import org.apache.coyote.response.HttpResponseStatusLine;
import org.apache.coyote.response.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPageHandler implements Handler {

  private static final Logger log = LoggerFactory.getLogger(LoginPageHandler.class);
  private static final String REDIRECT_URL = "http://localhost:8080/index.html";
  private static final String FILE_PATH = "static/login.html";

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return httpRequest.isGetMethod() && httpRequest.isStartWith("/login");
  }

  @Override
  public void handle(
      final HttpRequest httpRequest,
      final HttpResponse httpResponse
  ) throws IOException {

    httpRequest.getSession().getAttribute("user")
        .ifPresentOrElse(
            user -> redirectOnAlreadyLogin(httpRequest, httpResponse),
            () -> handleNoLoginRequest(httpResponse)
        );
  }

  private void redirectOnAlreadyLogin(
      final HttpRequest httpRequest,
      final HttpResponse httpResponse
  ) {
    final HttpResponseStatusLine statusLine = HttpResponseStatusLine.redirect();
    final HttpResponseHeader header = new HttpResponseHeader()
        .addCookie(httpRequest.getCookie())
        .sendRedirect(REDIRECT_URL);

    httpResponse.setHttpResponseHeader(header);
    httpResponse.setHttpResponseStatusLine(statusLine);
  }

  private void handleNoLoginRequest(final HttpResponse httpResponse) {
    try {
      final String responseBody = FileDetector.detect(FILE_PATH);

      final HttpResponseStatusLine statusLine = HttpResponseStatusLine.ok();
      final HttpResponseHeader header = new HttpResponseHeader()
          .addContentType(ContentType.TEXT_HTML, Charset.UTF_8);

      httpResponse.setResponseBody(new ResponseBody(responseBody));
      httpResponse.setHttpResponseStatusLine(statusLine);
      httpResponse.setHttpResponseHeader(header);
    } catch (IOException e) {
      log.error("파일 읽으면서 에러 발생", e);
    }
  }
}
