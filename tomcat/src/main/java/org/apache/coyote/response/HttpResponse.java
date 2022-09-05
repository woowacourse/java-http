package org.apache.coyote.response;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.coyote.response.StatusCode.FOUND;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private final StatusCode statusCode;
    private final ContentType contentType;
    private final String responseBody;
    private final String location;

    public HttpResponse(final StatusCode statusCode, final ContentType contentType, final String responseBody) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
        this.location = "";
    }

    public HttpResponse(final StatusCode statusCode, final ContentType contentType, final String responseBody,
                        final String location) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
        this.location = location;
    }

    public static HttpResponse of(final StatusCode statusCode, final ContentType contentType, final String location) {
        final String responseBody = new String(Objects.requireNonNull(readAllFile(location)), UTF_8);

        return new HttpResponse(statusCode, contentType, responseBody, location);
    }

    public static HttpResponse of(final StatusCode statusCode, final ContentType contentType, final String location,
                                  final String requestUrl) {
        final String responseBody = new String(Objects.requireNonNull(readAllFile(requestUrl)), UTF_8);

        return new HttpResponse(statusCode, contentType, responseBody, location);
    }

    public String getResponse() {
        if (statusCode.equals(FOUND)) {
            return String.join("\r\n",
                    "HTTP/1.1 " + statusCode + " ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "Location: " + location + " ",
                    "",
                    responseBody);
        }

        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private static byte[] readAllFile(final String requestUrl) {
        final URL resourceUrl = ClassLoader.getSystemResource("static" + requestUrl);
        final Path path = new File(resourceUrl.getPath()).toPath();

        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("파일을 읽어들이지 못했습니다.");
            return null;
        }
    }
}
