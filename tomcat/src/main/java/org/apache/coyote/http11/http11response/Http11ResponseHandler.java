package org.apache.coyote.http11.http11response;

import org.apache.coyote.http11.dto.ResponseComponent;

public class Http11ResponseHandler {

    private static final String LINE = "\r\n";

    public String makeResponse(ResponseComponent responseComponent) {
        return String.join(LINE,
                String.format("HTTP/1.1 %d %s ", responseComponent.getStatusCode().getCode(), responseComponent.getStatusCode()),
                String.format("Content-Type: %s;charset=utf-8 ", responseComponent.getContentType()),
                String.format("Content-Length: %s ", responseComponent.getContentLength()),
                "",
                responseComponent.getBody());
    }
}
