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
import org.apache.coyote.response.ResponseCookie;

public class LoginPageHandler implements Handler {

  private static final String REDIRECT_URL = "http://localhost:8080/index.html";

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return httpRequest.isGetMethod() && httpRequest.isStartWith("/login");
  }

  @Override
  public void handle(
      final HttpRequest httpRequest,
      final HttpResponse httpResponse
  ) throws IOException {

    if (httpRequest.hasCookie()) {
      final HttpResponseStatusLine statusLine = HttpResponseStatusLine.redirect();
      final HttpResponseHeader header = new HttpResponseHeader()
          .addCookie(new ResponseCookie(httpRequest.getCookie().getValue()))
          .sendRedirect(REDIRECT_URL);

      httpResponse.setHttpResponseHeader(header);
      httpResponse.setHttpResponseStatusLine(statusLine);

      return;
    }

    final String responseBody = FileDetector.detect("static/login.html");

    final HttpResponseStatusLine statusLine = HttpResponseStatusLine.ok();
    final HttpResponseHeader header = new HttpResponseHeader()
        .addContentType(ContentType.TEXT_HTML, Charset.UTF_8);

    httpResponse.setResponseBody(new ResponseBody(responseBody));
    httpResponse.setHttpResponseStatusLine(statusLine);
    httpResponse.setHttpResponseHeader(header);
  }
}
