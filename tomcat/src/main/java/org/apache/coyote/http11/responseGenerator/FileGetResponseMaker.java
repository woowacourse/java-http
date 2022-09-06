package org.apache.coyote.http11.responseGenerator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class FileGetResponseMaker implements ResponseMaker {

    private static final int CONTENT_TYPE_START_INDEX = 1;

    @Override
    public String createResponse(final HttpRequest httpRequest)
            throws URISyntaxException, IOException {
        final String requestUrl = httpRequest.getRequestUrl();
        final URL resource =
                this.getClass().getClassLoader().getResource("static" + requestUrl);
        final String extension = requestUrl.split("\\.")[CONTENT_TYPE_START_INDEX];
        final Path path = Paths.get(resource.toURI());
        final var responseBody = new String(Files.readAllBytes(path));
        final ContentType contentType = ContentType.findContentType(extension);

        final HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, responseBody, contentType);
        return httpResponse.toString();
    }
}
