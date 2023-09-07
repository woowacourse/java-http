package org.apache.coyote.handler;

import java.io.IOException;
import org.apache.coyote.detector.FileDetector;
import org.apache.coyote.parser.StaticFileParser;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.Charset;
import org.apache.coyote.response.ContentType;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpResponseHeader;
import org.apache.coyote.response.HttpResponseStatusLine;
import org.apache.coyote.response.ResponseBody;

public class StaticFileHandler implements StaticHandler {

  private static final String PREFIX_PATH_STATIC_FILE = "static";

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return StaticFileParser.isStaticFile(httpRequest.getUri());
  }

  @Override
  public void handle(
      final HttpRequest httpRequest,
      final HttpResponse httpResponse
  ) throws IOException {
    final String uri = httpRequest.getUri();
    final String bodyData = FileDetector.detect(PREFIX_PATH_STATIC_FILE + uri);
    final String typeData = StaticFileParser.parsingFileType(uri);

    final HttpResponseHeader header = new HttpResponseHeader()
        .addContentType(ContentType.findTypeFrom(typeData), Charset.UTF_8);
    final HttpResponseStatusLine statusLine = HttpResponseStatusLine.ok();
    final ResponseBody responseBody = new ResponseBody(bodyData);

    httpResponse.setResponseBody(responseBody);
    httpResponse.setHttpResponseHeader(header);
    httpResponse.setHttpResponseStatusLine(statusLine);
  }
}
