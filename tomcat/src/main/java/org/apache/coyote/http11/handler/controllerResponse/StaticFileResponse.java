package org.apache.coyote.http11.handler.controllerResponse;

import java.util.HashMap;
import org.apache.coyote.http11.general.HttpHeaders;
import org.apache.coyote.http11.general.HttpProtocolVersion;
import org.apache.coyote.http11.handler.StaticFileHandler;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.httpResponse.HttpStatus;

public record StaticFileResponse(HttpStatus status, HttpHeaders headers, String content) implements ApplicationResponse {

    public StaticFileResponse(HttpStatus status, String content) {
        this(status, new HttpHeaders(new HashMap<>()), content);
    }

    @Override
    public void addHeader(String key, String value) {
        headers.add(key, value);
    }

    @Override
    public HttpResponse toHttpResponse(HttpProtocolVersion protocolVersion) {
        return StaticFileHandler.handleDefault(protocolVersion, content);
    }
}
