package org.apache.coyote.handler;

import java.io.IOException;
import org.apache.coyote.detector.FileDetector;
import org.apache.coyote.request.HttpRequest;

public class LoginPageHandler implements Handler {

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return httpRequest.isGetMethod() && httpRequest.isStartWith("/login");
  }

  @Override
  public String handle(final HttpRequest httpRequest) throws IOException {
    if (httpRequest.hasCookie()) {
      return String.join("\r\n",
          "HTTP/1.1 302 FOUND ",
          "Set-Cookie: " + httpRequest.getCookie(),
          "Location: http://localhost:8080/index.html ");
    }

    final String responseBody = FileDetector.detect("static/login.html");

    return String.join("\r\n",
        "HTTP/1.1 200 OK ",
        "Content-Type: text/html;charset=utf-8 ",
        "Content-Length: " + responseBody.getBytes().length + " ",
        "",
        responseBody);
  }
}
