package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponse {

    private static final String NEW_LINE = "\r\n";
    private static final String STATIC = "static";
    private final ResponseLine responseLine;
    private final HttpHeaders headers;
    private final String responseBody;

    private HttpResponse(final ResponseLine responseLine, final HttpHeaders headers, final String responseBody) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse parse(final HttpRequest request) throws IOException {
        final byte[] content = readAllByte(request);
        final HttpHeaders headers = HttpHeaders.createResponse(content);
        final String responseBody = new String(content);

        return new HttpResponse(ResponseLine.create(), headers, responseBody);
    }

    private static byte[] readAllByte(final HttpRequest request) throws IOException {
        final String uri = request.getUri();
        final URL url = HttpResponse.class.getClassLoader()
                .getResource(STATIC + uri);
        final Path path = new File(url.getPath())
                .toPath();

        return Files.readAllBytes(path);
    }

    @Override
    public String toString() {
        return responseLine.toString() +
                NEW_LINE +
                headers.toString() +
                NEW_LINE +
                responseBody;
    }
}
