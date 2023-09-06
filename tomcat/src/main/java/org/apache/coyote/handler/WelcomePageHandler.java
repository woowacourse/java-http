package org.apache.coyote.handler;

import org.apache.coyote.request.HttpRequest;

public class WelcomePageHandler implements StaticHandler {

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return httpRequest.isGetMethod() && httpRequest.isSameUri("/");
  }

  @Override
  public String handle(final HttpRequest httpRequest) {
    final String responseBody = "Hello world!";

    return String.join("\r\n",
        "HTTP/1.1 200 OK ",
        "Content-Type: text/html;charset=utf-8 ",
        "Content-Length: " + responseBody.getBytes().length + " ",
        "",
        responseBody);
  }
}
