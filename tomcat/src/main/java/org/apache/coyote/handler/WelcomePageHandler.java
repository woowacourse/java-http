package org.apache.coyote.handler;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.Charset;
import org.apache.coyote.response.ContentType;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpResponseHeader;
import org.apache.coyote.response.HttpResponseStatusLine;
import org.apache.coyote.response.ResponseBody;

public class WelcomePageHandler implements StaticHandler {

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return httpRequest.isGetMethod() && httpRequest.isSameUri("/");
  }

  @Override
  public void handle(final HttpRequest httpRequest, final HttpResponse httpResponse) {
    final String bodyData = "Hello world!";

    final HttpResponseHeader header = new HttpResponseHeader()
        .addContentType(ContentType.TEXT_HTML, Charset.UTF_8);
    final HttpResponseStatusLine statusLine = HttpResponseStatusLine.ok();
    final ResponseBody responseBody = new ResponseBody(bodyData);

    httpResponse.setResponseBody(responseBody);
    httpResponse.setHttpResponseHeader(header);
    httpResponse.setHttpResponseStatusLine(statusLine);
  }
}
