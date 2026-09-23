package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.coyote.http11.Headers;

public class HttpResponse {

    private final String protocolVersion = "HTTP/1.1";
    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final Headers headers;
    private final String responseBody;

    public HttpResponse(HttpStatus httpStatus, ContentType contentType, String responseBody) {
        this(httpStatus, contentType, new Headers(), responseBody);
    }

    public HttpResponse(HttpStatus httpStatus, ContentType contentType, Headers headers, String responseBody) {
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse ok(final String name) throws IOException {
        return new HttpResponse(
                HttpStatus.OK,
                ContentType.HTML,
                Files.readString(resolveResource(name).toPath())
        );
    }

    private static File resolveResource(final String name) {
        final String resourceName = "static" + name;
        final URL resource = Objects.requireNonNull(
                HttpResponse.class.getClassLoader().getResource(resourceName),
                "리소스를 찾을 수 없음: " + resourceName
        );
        return new File(resource.getFile());
    }

    public void addHeader(final String name, final String value) {
        headers.add(name, value);
    }

    public byte[] getBytes() {
        final List<String> responseLines = new ArrayList<>();
        responseLines.add(protocolVersion + " " + httpStatus.statusCode() + " " + httpStatus.name());
        responseLines.add("Content-Type: " + contentType.value() + ";charset=utf-8");
        responseLines.add("Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length);
        headers.entries().forEach((name, value) -> responseLines.add(name + ": " + value));
        responseLines.add("");
        responseLines.add(responseBody);

        return String.join("\r\n", responseLines).getBytes(StandardCharsets.UTF_8);
    }

}
