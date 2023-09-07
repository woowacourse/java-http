package org.apache.coyote.http11;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.StartLine;
import org.apache.coyote.http11.response.StatusCode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

public class ResponseGenerator {

    public static final String ROOT_URI = "/";
    private static final String STATIC_PATH = "static";

    private ResponseGenerator() {
    }

    public static String generate(final Request request) {
        try {
            if (ROOT_URI.equals(request.getUri())) {
                final Response response = getDefaultResponse();
                return response.toMessage();
            }

            final Response response = getFileResponse(request);
            return response.toMessage();
        } catch (final Exception e) {
            return null;
        }
    }

    private static Response getDefaultResponse() {
        final StartLine startLine = new StartLine(HttpVersion.HTTP_1_1, StatusCode.OK);
        final ContentType contentType = ContentType.HTML;
        final String responseBody = "Hello world!";

        return Response.of(startLine, contentType, responseBody);
    }

    private static Response getFileResponse(final Request request) throws IOException {
        final StartLine startLine = new StartLine(HttpVersion.HTTP_1_1, StatusCode.OK);
        final ContentType contentType = ContentType.findBy(request.getUri());
        final String responseBody = getFileToResponseBody(request.getUri());

        return Response.of(startLine, contentType, responseBody);
    }

    private static String getFileToResponseBody(final String fileName) throws IOException {
        final String path = STATIC_PATH + fileName;
        final URL resource = ClassLoader.getSystemClassLoader().getResource(path);
        final String filePath = Objects.requireNonNull(resource).getPath();
        final File file = new File(URLDecoder.decode(filePath, StandardCharsets.UTF_8));

        return new String(Files.readAllBytes(file.toPath()));
    }
}
