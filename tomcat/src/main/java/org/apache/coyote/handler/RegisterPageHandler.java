package org.apache.coyote.handler;

import java.io.IOException;
import org.apache.coyote.detector.FileDetector;
import org.apache.coyote.request.HttpRequest;

public class RegisterPageHandler implements StaticHandler {

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return httpRequest.isGetMethod() && httpRequest.isSameUri("/register");
  }

  @Override
  public String handle(final HttpRequest httpRequest) throws IOException {
    final String responseBody = FileDetector.detect("static/register.html");

    return String.join("\r\n",
        "HTTP/1.1 200 OK ",
        "Content-Type: text/html;charset=utf-8 ",
        "Content-Length: " + responseBody.getBytes().length + " ",
        "",
        responseBody);
  }
}
