package org.apache.coyote.http11.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.exception.NotFoundResourceException;

public class HttpMessageSupporter {
    private static final String ROOT = "/";
    private static final String BLANK = " ";
    private static final int REQUEST_RESOURCE_INDEX = 1;
    private static final String HTML_404 = "/404.html";
    private static final String INDEX_HTML = "/index.html";

    public static String getRequestURI(final String requestLine) {
        final String requestURI = requestLine.split(BLANK)[REQUEST_RESOURCE_INDEX];

        if (ROOT.equals(requestURI)) {
            return INDEX_HTML;
        }
        return requestURI;
    }

    public static String getHttpMessage(final String requestURI) throws IOException {
        final var absolutePath = parsePath(requestURI);
        final var contentType = Files.probeContentType(absolutePath);
        final var responseBody = readFile(absolutePath);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + BLANK,
                "",
                responseBody);
    }

    private static Path parsePath(final String requestURI) {
        try {
            return PathParser.parsePath(requestURI);
        } catch (NotFoundResourceException e) {
            return PathParser.parsePath(HTML_404);
        }
    }

    private static String readFile(final Path filePath) throws IOException {
        return String.join("\n", Files.readAllLines(filePath)) + "\n";
    }
}
