package org.apache.coyote.http11.http;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.coyote.http11.util.HttpStatus;
import org.apache.coyote.http11.util.PathParser;

public class HttpResponse {
    private static final String SPACE = " ";
    private static final String RESOURCE_SEPARATOR = "/";
    private static final String HTML_EXTENSION = ".html";

    private String resourceURI;
    private HttpStatus httpStatus;
    private String location;
    private Cookies cookies;

    public String getString() {
        return null;
    }

    public byte[] getBytes() throws IOException {
        return getHttpMessage().getBytes();
    }

    private String getHttpMessage() throws IOException {
        if (Objects.isNull(resourceURI)) {
            if (Objects.isNull(cookies)) {
                return String.join("\r\n",
                        "HTTP/1.1 " + httpStatus.getValue() + SPACE,
                        "Location: " + location + SPACE
                );
            }
            return String.join("\r\n",
                    "HTTP/1.1 " + httpStatus.getValue() + SPACE,
                    "Location: " + location + SPACE,
                    cookies.parseToHttpMessage()
            );
        }
        final var absolutePath = PathParser.parsePath(resourceURI);
        final var contentType = Files.probeContentType(absolutePath);
        final var responseBody = readFile(absolutePath);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + SPACE,
                "",
                responseBody);
    }

    private String readFile(final Path filePath) throws IOException {
        return String.join("\n", Files.readAllLines(filePath)) + "\n";
    }

    public void setResourceURI(final String resourceURI) {
        this.resourceURI = resourceURI;
    }

    public void setStatusCode(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setLocation(final String location) {
        this.location = RESOURCE_SEPARATOR + location + HTML_EXTENSION;
    }

    public void addCookie(final Cookies cookies) {
        this.cookies = cookies;
    }
}
