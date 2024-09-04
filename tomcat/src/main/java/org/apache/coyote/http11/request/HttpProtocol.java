package org.apache.coyote.http11.request;

import com.techcourse.exception.UncheckedServletException;
import java.util.Arrays;

public enum HttpProtocol {

    HTTP_11("HTTP/1.1");

    private final String httpMessage;

    HttpProtocol(final String httpMessage) {
        this.httpMessage = httpMessage;
    }

    public String getHttpMessage() {
        return httpMessage;
    }

    public static HttpProtocol from(String protocolMessage) {
        return Arrays.stream(HttpProtocol.values())
                .filter(httpProtocol -> httpProtocol.httpMessage.equals(protocolMessage))
                .findAny()
                .orElseThrow(() -> new UncheckedServletException("지원하지 않는 http 프로토콜 버젼입니다"));
    }
}
