package org.apache.coyote.http11.handlermapper;

import java.io.IOException;
import java.util.Arrays;
import org.apache.coyote.http11.handler.FileUriHandler;
import org.apache.coyote.http11.handler.HandlerResponse;
import org.apache.coyote.http11.handler.LoginUriHandler;
import org.apache.coyote.http11.handler.RootUriHandler;
import org.apache.coyote.http11.handler.UriHandler;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public enum HandlerManager {
    ROOT(new RootUriHandler()),
    LOGIN(new LoginUriHandler()),
    FILE(new FileUriHandler());

    private final UriHandler uriHandler;

    HandlerManager(UriHandler uriHandler) {
        this.uriHandler = uriHandler;
    }

    public static HandlerResponse getUriResponse(HttpRequest httpRequest) throws IOException {
        UriHandler handler = getHandler(httpRequest);
        return handler.getResponse(httpRequest);
    }

    private static UriHandler getHandler(HttpRequest httpRequest) {
        return Arrays.stream(HandlerManager.values())
                .map(HandlerManager::getUriHandler)
                .filter(uriHandler -> uriHandler.canHandle(httpRequest))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("처리할 수 없는 요청입니다."));
    }

    public UriHandler getUriHandler() {
        return uriHandler;
    }
}

