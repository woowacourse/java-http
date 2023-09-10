package org.apache.coyote.http11;

import org.apache.coyote.http11.response.HttpResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseGenerator {

    private static final String STATIC = "static";

    public static String makeResponse(final HttpResponse response) throws IOException {
        if (response.containBody()) {
            return makeHttpResponse(response, response.getBody());
        }
        final var resource = ResponseGenerator.class.getClassLoader().getResource(STATIC + response.getRedirect());
        final String responseBody = makeResponseBody(resource);
        return makeHttpResponse(response, responseBody);
    }

    private static String makeHttpResponse(final HttpResponse response, final String responseBody) {
        final StringBuilder httpResponse = new StringBuilder();

        httpResponse.append("HTTP/1.1 ").append(response.getStatusCode().getResponse()).append("\r\n");
        httpResponse.append("Content-Type: ").append(response.getContentType().getType()).append(";charset=utf-8\r\n");
        httpResponse.append("Content-Length: ").append(responseBody.getBytes().length).append("\r\n");

        if (response.containJsessionId()) {
            final Map<String, String> value = response.getCookie().getValue();
            httpResponse.append("Set-Cookie: ").append("JSESSIONID" + "=" + value.get("JSESSIONID")).append("\r\n");
        }

        if (!response.getOtherHeader().isEmpty()) {
            final String additionalHeaders = response.getOtherHeader().entrySet().stream()
                    .map(entry -> entry.getKey() + ": " + entry.getValue())
                    .collect(Collectors.joining("\r\n"));
            httpResponse.append(additionalHeaders).append("\r\n");
        }

        httpResponse.append("\r\n");
        httpResponse.append(responseBody);

        return httpResponse.toString();
    }

    private static String makeResponseBody(final URL resource) throws IOException {
        final var actualFilePath = new File(resource.getPath()).toPath();
        final var fileBytes = Files.readAllBytes(actualFilePath);
        return new String(fileBytes, StandardCharsets.UTF_8);
    }
}
