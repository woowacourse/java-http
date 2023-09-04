package org.apache.coyote.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.parser.StaticFileParser;

public class StaticFileHandler implements StaticHandler {

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return StaticFileParser.isStaticFile(httpRequest.getPath());
  }

  @Override
  public String handle(final HttpRequest httpRequest) throws IOException {
    final String path = httpRequest.getPath();

    final URL resource = getClass()
        .getClassLoader()
        .getResource("static" + path);

    final String responseBody = new String(
        Files.readAllBytes(new File(resource.getFile()).toPath()));

    return String.join("\r\n",
        "HTTP/1.1 200 OK ",
        "Content-Type: " + StaticFileParser.parsingFileType(path) + ";charset=utf-8 ",
        "Content-Length: " + responseBody.getBytes().length + " ",
        "",
        responseBody);
  }
}
