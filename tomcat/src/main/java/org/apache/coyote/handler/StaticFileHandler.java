package org.apache.coyote.handler;

import java.io.IOException;
import org.apache.coyote.detector.FileDetector;
import org.apache.coyote.parser.StaticFileParser;
import org.apache.coyote.request.HttpRequest;

public class StaticFileHandler implements StaticHandler {

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return StaticFileParser.isStaticFile(httpRequest.getUri());
  }

  @Override
  public String handle(final HttpRequest httpRequest) throws IOException {
    final String uri = httpRequest.getUri();

    final String responseBody = FileDetector.detect("static" + uri);

    return String.join("\r\n",
        "HTTP/1.1 200 OK ",
        "Content-Type: " + StaticFileParser.parsingFileType(uri) + ";charset=utf-8 ",
        "Content-Length: " + responseBody.getBytes().length + " ",
        "",
        responseBody);
  }
}
