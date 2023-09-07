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

public class RegisterPageHandler implements StaticHandler {

  private static final String RENDERING_FILE_NAME = "static/register.html";

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return httpRequest.isGetMethod() && httpRequest.isSameUri("/register");
  }

  @Override
  public void handle(
      final HttpRequest httpRequest,
      final HttpResponse httpResponse
  ) throws IOException {
    final String bodyData = FileDetector.detect(RENDERING_FILE_NAME);

    final HttpResponseHeader header = new HttpResponseHeader()
        .addContentType(ContentType.TEXT_HTML, Charset.UTF_8);
    final HttpResponseStatusLine statusLine = HttpResponseStatusLine.ok();
    final ResponseBody responseBody = new ResponseBody(bodyData);

    httpResponse.setResponseBody(responseBody);
    httpResponse.setHttpResponseHeader(header);
    httpResponse.setHttpResponseStatusLine(statusLine);
  }
}
