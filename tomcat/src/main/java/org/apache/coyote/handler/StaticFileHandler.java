package org.apache.coyote.handler;

import java.io.IOException;
import org.apache.coyote.detector.FileDetector;
import org.apache.coyote.parser.StaticFileParser;
import org.apache.coyote.request.HttpRequest;

public class StaticFileHandler implements StaticHandler {

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return StaticFileParser.isStaticFile(httpRequest.getPath());
  }

  @Override
  public String handle(final HttpRequest httpRequest) throws IOException {
    final String path = httpRequest.getPath();

    final String responseBody = FileDetector.detect("static" + path);

    return String.join("\r\n",
        "HTTP/1.1 200 OK ",
        "Content-Type: " + StaticFileParser.parsingFileType(path) + ";charset=utf-8 ",
        "Content-Length: " + responseBody.getBytes().length + " ",
        "",
        responseBody);
  }
}
