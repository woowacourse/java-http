package org.apache.coyote.http11;

import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private String responseLine;
    private final Map<String, String> responseHeaders;
    private String responseBody;

    public HttpResponse(
            final String responseLine,
            final Map<String, String> responseHeaders,
            final String responseBody
    ) {
        this.responseLine = responseLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public static HttpResponse createWelcomeHttpResponse() {
        String welcomeResponseBody = "Hello world!";
        return new HttpResponse(
                "HTTP/1.1 200 OK",
                Map.of(
                        "Content-Type", "text/html",
                        "Content-Length", String.valueOf(welcomeResponseBody.length())
                ),
                welcomeResponseBody
        );
    }

    public String parseHttpResponse() {
        final String parsedHeaders = responseHeaders.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));

        return String.join("\r\n",
                responseLine + " ",
                parsedHeaders,
                "",
                responseBody);
    }
}
