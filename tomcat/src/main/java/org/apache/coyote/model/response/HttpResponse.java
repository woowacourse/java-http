package org.apache.coyote.model.response;

import org.apache.coyote.exception.NotFoundFileException;
import org.apache.coyote.model.session.Cookie;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.apache.coyote.model.request.ContentType.HTML;

public class HttpResponse {

    private static final String STATIC = "static";

    private final ResponseLine responseLine;
    private final ResponseHeader responseHeader;
    private final String body;

    private HttpResponse(final ResponseLine responseLine, final ResponseHeader responseHeader, final String body) {
        this.responseLine = responseLine;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    public static HttpResponse of(final String extension, final String body, final ResponseLine responseLine) {
        final ResponseHeader responseHeader = createHeaders(extension, body);
        return new HttpResponse(responseLine, responseHeader, body);
    }

    private static ResponseHeader createHeaders(final String extension, final String body) {
        final Map<String, String> headers = new HashMap<>();
        headers.put(ResponseHeader.CONTENT_TYPE, extension);
        headers.put(ResponseHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return ResponseHeader.of(headers);
    }

    public static String getResponseBody(final String uri, final Class<?> ClassType) {
        try {
            final URL url = Objects.requireNonNull(ClassType.getClassLoader().getResource(STATIC + uri));
            final Path path = Paths.get(url.toURI());
            return new String(Files.readAllBytes(path));
        } catch (Exception e) {
            throw new NotFoundFileException("파일 찾기에 실패했습니다.");
        }
    }

    public static HttpResponse createResponse(final StatusCode statusCode, final String responseBody) {
        ResponseLine responseLine = ResponseLine.of(statusCode);
        return HttpResponse.of(HTML.getExtension(), responseBody, responseLine);
    }

    public void addCookie(final Cookie cookie) {
        responseHeader.addCookie(cookie);
    }

    public String getResponse() {
        return String.join("\r\n",
                responseLine.getResponse(),
                getResponseHeaders(),
                "",
                body);
    }

    private String getResponseHeaders() {
        return responseHeader.getResponseHeaders();
    }

    public String getBody() {
        return body;
    }

    public void setHeader(String location, String value) {
        responseHeader.setHeader(location, value);
    }
}
