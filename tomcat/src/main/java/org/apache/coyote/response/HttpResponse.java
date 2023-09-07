package org.apache.coyote.response;

import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

  private HttpResponseStatusLine httpResponseStatusLine;
  private HttpResponseHeader httpResponseHeader;
  private ResponseBody responseBody;

  public void setHttpResponseStatusLine(
      final HttpResponseStatusLine httpResponseStatusLine
  ) {
    this.httpResponseStatusLine = httpResponseStatusLine;
  }

  public void setHttpResponseHeader(final HttpResponseHeader httpResponseHeader) {
    this.httpResponseHeader = httpResponseHeader;
  }

  public void setResponseBody(final ResponseBody responseBody) {
    this.responseBody = responseBody;
  }

  public String read() {
    List<String> responseParts = new ArrayList<>();
    responseParts.add(httpResponseStatusLine.read());
    responseParts.add(httpResponseHeader.read());

    if (responseBody != null && responseBody.isNotEmpty()) {
      responseParts.add("Content-Length: " + responseBody.calculateContentLength());
      responseParts.add("");
      responseParts.add(responseBody.read());
    }

    return String.join("\r\n", responseParts);
  }
}
