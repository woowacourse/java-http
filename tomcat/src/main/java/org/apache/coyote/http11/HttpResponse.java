package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeaders.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpHeaders.CONTENT_TYPE_CSS;
import static org.apache.coyote.http11.HttpHeaders.CONTENT_TYPE_UTF_8;
import static org.apache.coyote.http11.HttpHeaders.HTTP_LINE_SUFFIX;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import nextstep.jwp.exception.UncheckedServletException;

public class HttpResponse {

    private static final int MAX_DEPTH = 3;
    private static final String PATH_DELIMITER = "/";
    private static final String RESOURCE_ROOT_PATH = "static";

    private final HttpResponseStatusLine statusLine;
    private final HttpHeaders headers;
    private final String body;

    private HttpResponse(HttpResponseStatusLine statusLine, HttpHeaders headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse from(HttpRequest request) throws IOException {
        var requestHeaders = request.getHeaders();
        var responseBody = "";

        if (request.getMethod() == HttpMethod.GET) {
            responseBody = findFileContentByPath(request.getPath());
        }

        final var statusLine = new HttpResponseStatusLine(HttpStatusCode.OK);
        final var responseHeaders = buildResponseHeaders(responseBody, requestHeaders);

        return new HttpResponse(statusLine, responseHeaders, responseBody);
    }

    private static HttpHeaders buildResponseHeaders(String responseBody, HttpHeaders requestHeaders) {
        var responseHeaders = new HttpHeaders(new HashMap<>());
        responseHeaders.put(CONTENT_TYPE, CONTENT_TYPE_UTF_8);
        responseHeaders.put(CONTENT_LENGTH, String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));

        if (requestHeaders.isAcceptCss()) {
            responseHeaders.put(CONTENT_TYPE, CONTENT_TYPE_CSS);
        }
        return responseHeaders;
    }

    private static String findFileContentByPath(String requestPath) throws IOException {
        final var resourcePath = HttpResponse.class.getClassLoader().getResource(RESOURCE_ROOT_PATH).getPath();
        final var fileName = requestPath.substring(requestPath.lastIndexOf(PATH_DELIMITER) + 1);
        final var filePath = findAbsolutePath(resourcePath, fileName);

        try (final var inputStream = Files.newInputStream(filePath)) {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    private static Path findAbsolutePath(String resourcePath, String fileName) throws IOException {
        return Files.find(
                            Path.of(resourcePath),
                            MAX_DEPTH,
                            (path, basicFileAttributes) -> basicFileAttributes.isRegularFile() && path.toFile().getName().contains(fileName)
                    )
                    .findFirst()
                    .orElseThrow(() -> new NoSuchFileException(fileName + "에 해당 하는 파일을 찾을 수 없습니다."));
    }

    public byte[] getBytes() {
        return toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return String.join(HTTP_LINE_SUFFIX, statusLine.toString(), headers.toString(), body);
    }
}
