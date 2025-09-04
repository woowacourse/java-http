package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final Map<String, String> MIME_TYPES = Map.of(
            "html", "text/html",
            "css", "text/css",
            "js", "text/javascript"
    );

    private String responseLine;
    private final Map<String, String> responseHeaders;
    private byte[] responseBody;

    public HttpResponse(
            final String responseLine,
            final Map<String, String> responseHeaders,
            final byte[] responseBody
    ) {
        this.responseLine = responseLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public static HttpResponse createWelcomeHttpResponse() {
        final byte[] bodyBytes = "Hello world!".getBytes(StandardCharsets.UTF_8);
        final Map<String, String> responseHeaders = new LinkedHashMap<>();
        responseHeaders.put("Content-Type", getContentType("html"));
        responseHeaders.put("Content-Length", String.valueOf(bodyBytes.length));

        return new HttpResponse("HTTP/1.1 200 OK", responseHeaders, bodyBytes);
    }

    public static String getContentType(final String resourceExtension) {
        final String mimeType = MIME_TYPES.get(resourceExtension);
        if (mimeType.equals("text/html")) {
            return mimeType + ";charset=utf-8";
        }
        return mimeType;
    }

    public String parseHttpResponse() {
        final String parsedHeaders = responseHeaders.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));

        return String.join("\r\n",
                responseLine + " ",
                parsedHeaders,
                "",
                new String(responseBody, StandardCharsets.UTF_8));
    }
}
