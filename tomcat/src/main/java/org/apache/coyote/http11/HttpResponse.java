package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
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
        final String welcomeResponseBody = "Hello world!";
        final Map<String, String> responseHeaders = new LinkedHashMap<>();
        responseHeaders.put("Content-Type", "text/html;charset=utf-8");
        byte[] bodyBytes = welcomeResponseBody.getBytes(StandardCharsets.UTF_8);
        responseHeaders.put("Content-Length", String.valueOf(bodyBytes.length));

        return new HttpResponse("HTTP/1.1 200 OK", responseHeaders, welcomeResponseBody);
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
