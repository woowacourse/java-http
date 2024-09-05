package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequest {

    private final String method;
    private final String uri;
    private final String version;
    private final String acceptLine;

    private HttpRequest(final String method, final String uri, final String version, final String acceptLine) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.acceptLine = acceptLine;
    }

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final String requestLine = bufferedReader.readLine();

        final String[] requestParts = requestLine.split(" ");
        final String httpMethod = requestParts[0];
        final String uri = resolveDefaultPage(requestParts[1]);
        final String version = requestParts[2];
        final String acceptLine = findAcceptLine(bufferedReader);
        return new HttpRequest(httpMethod, uri, version, acceptLine);
    }

    private static String resolveDefaultPage(final String endPoint) {
        if (endPoint.equals("/") || endPoint.isEmpty()) {
            return "/index.html";
        }
        return endPoint;
    }

    private static String findAcceptLine(final BufferedReader bufferedReader) {
        return bufferedReader.lines()
                .filter(line -> line.startsWith("Accept:"))
                .map(line -> line.substring(8).trim())
                .findFirst()
                .orElse("*/*");
    }

    public String getUri() {
        return uri;
    }

    public String getAcceptLine() {
        return acceptLine;
    }
}
