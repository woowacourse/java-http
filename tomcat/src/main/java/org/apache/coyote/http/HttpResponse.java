package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String WELCOME_MESSAGE = "Hello world!";

    private final List<String> values;
    private final String responseBody;

    private HttpResponse(final List<String> values, final String responseBody) {
        this.values = values;
        this.responseBody = responseBody;
    }

    public static HttpResponse from(final HttpRequest httpRequest) throws IOException {
        final List<String> values = new ArrayList<>();
        final String responseBody = getResponseBody(httpRequest.getPath());

        values.add(HttpStatusCode.OK.getResponseStartLine());
        values.add("Content-Type: " + httpRequest.getContentType() + ";charset=utf-8 ");
        values.add("Content-Length: " + responseBody.getBytes().length + " ");

        return new HttpResponse(values, responseBody);
    }

    private static String getResponseBody(final String path) throws IOException {
        if (path.equals("/")) {
            return WELCOME_MESSAGE;
        }

        String resourcePath = "static/" + path;
        if (!resourcePath.contains(".")) {
            resourcePath += ".html";
        }

        final String resource = HttpResponse.class.getClassLoader()
                .getResource(resourcePath)
                .getPath();
        final File file = new File(resource);
        final BufferedReader fileReader = new BufferedReader(new FileReader(file));
        String responseBody = fileReader.lines()
                .collect(Collectors.joining("\n"));
        responseBody += "\n";

        fileReader.close();

        return responseBody;
    }

    public HttpResponse changeStatusCode(final HttpStatusCode httpStatusCode) {
        values.set(0, httpStatusCode.getResponseStartLine());
        return this;
    }

    public HttpResponse setLocationAsHome() {
        values.add("Location: /index.html ");
        return this;
    }

    public HttpResponse setSessionId(final String sessionId) {
        values.add("Set-Cookie: JSESSIONID=" + sessionId + " ");
        return this;
    }

    public byte[] toResponseBytes() {
        values.add("");
        if (responseBody != null) {
            values.add(responseBody);
        }
        return String.join("\r\n", values)
                .getBytes();
    }
}
