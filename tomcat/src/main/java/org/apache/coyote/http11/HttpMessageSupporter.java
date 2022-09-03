package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpMessageSupporter {

    private static final String ROOT = "/";
    private static final String BLANK = " ";
    private static final int REQUEST_RESOURCE_INDEX = 1;

    public static String getRequestURI(final InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final var firstLineOfHttpRequest = bufferedReader.readLine();
        return firstLineOfHttpRequest.split(BLANK)[REQUEST_RESOURCE_INDEX];
    }

    public static String getHttpMessage(final String requestURI) throws IOException {
        final var absolutePath = PathParser.parsePath(requestURI);

        final var contentType = getContentType(absolutePath);

        var responseBody = readFile(requestURI, absolutePath);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + BLANK,
                "",
                responseBody);
    }

    private static String getContentType(final Path absolutePath) throws IOException {
        if (absolutePath.toString().equals(ROOT)) {
            return "text/html";
        }
        return Files.probeContentType(absolutePath);
    }

    private static String readFile(final String requestURI, final Path filePath) throws IOException {
        if (!requestURI.equals(ROOT)) {
            var responseBody = String.join("\n", Files.readAllLines(filePath));
            return responseBody + "\n";
        }
        return "Hello world!";
    }
}
