package org.apache.coyote.http;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HttpResponse {

    private final String version;
    private final HttpStatus status;
    private final ContentType type;
    private final String body;
    private final Map<String, String> headers;

    public HttpResponse(final String version, final HttpStatus status, final ContentType type, final String body) {
        this(version, status, type, body, new HashMap<>());
    }
    
    public static HttpResponse redirect(final String version, final String location) {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Location", location);
        return new HttpResponse(version, HttpStatus.FOUND, ContentType.HTML, "", headers);
    }

    @Override
    public String toString() {
        final int contentLength = body == null ? 0 : body.getBytes(StandardCharsets.UTF_8).length;
        final String bodyContent = body == null ? "" : body;
        
        final StringBuilder response = new StringBuilder();
        response.append("HTTP/").append(version)
                .append(" ").append(status.getCode())
                .append(" ").append(status.getReasonPhrase())
                .append("\r\n");
        
        response.append("Content-Type: ").append(type.getMimeType()).append("\r\n");
        response.append("Content-Length: ").append(contentLength).append("\r\n");
        
        headers.forEach((key, value) ->
            response.append(key).append(": ").append(value).append("\r\n"));
        
        response.append("\r\n").append(bodyContent);
        
        return response.toString();
    }
}
