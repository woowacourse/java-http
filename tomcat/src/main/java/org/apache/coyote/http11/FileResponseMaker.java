package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileResponseMaker implements ResponseMaker {

    private static final int CONTENT_TYPE_START_INDEX = 1;

    @Override
    public String createResponse(final String requestUrl)
            throws URISyntaxException, IOException {

        final URL resource =
                this.getClass().getClassLoader().getResource("static" + requestUrl);
        final String extension = requestUrl.split("\\.")[CONTENT_TYPE_START_INDEX];
        final Path path = Paths.get(resource.toURI());
        final var responseBody = new String(Files.readAllBytes(path));
        final ContentType contentType = ContentType.findContentType(extension);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getContentType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
