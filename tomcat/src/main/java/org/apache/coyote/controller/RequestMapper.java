package org.apache.coyote.controller;

import java.util.Arrays;
import org.apache.coyote.http11.httpmessage.request.requestline.RequestUri;

public enum RequestMapper {
    ROOT("/", new HelloWorldController()),
    INDEX("staticFiles", new ResourceFileController()),
    LOGIN("/login", new LoginController()),
    REGISTER("/register", new RegisterController());

    private final String uri;
    private final Controller controller;

    RequestMapper(String uri, Controller controller) {
        this.uri = uri;
        this.controller = controller;
    }

    public static Controller findController(RequestUri requestUri) {
        if (requestUri.isFileRequest()) {
            return INDEX.controller;
        }
        return Arrays.stream(values())
                .filter(it -> requestUri.isMatchUri(it.uri))
                .map(it -> it.controller)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("uri를 다시 확인해주세요."));
    }
}
