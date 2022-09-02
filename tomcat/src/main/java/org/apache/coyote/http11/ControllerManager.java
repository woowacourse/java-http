package org.apache.coyote.http11;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;
import org.apache.coyote.http11.uriprocessor.FileUriHandler;
import org.apache.coyote.http11.uriprocessor.LoginUriHandler;
import org.apache.coyote.http11.uriprocessor.RootUriHandler;
import org.apache.coyote.http11.uriprocessor.UriHandler;

public enum ControllerManager {
    ROOT(ControllerManager::isRootUri, new RootUriHandler()),
    LOGIN(ControllerManager::isLoginUri, new LoginUriHandler()),
    FILE(ControllerManager::isFileUri, new FileUriHandler());

    private final Predicate<String> condition;

    private final UriHandler uriProcessor;

    ControllerManager(Predicate<String> condition, UriHandler uriProcessor) {
        this.condition = condition;
        this.uriProcessor = uriProcessor;
    }

    private static boolean isRootUri(String uri) {
        return uri.equals("/");
    }

    private static boolean isLoginUri(String uri) {
        return uri.startsWith("/login?");
    }

    private static boolean isFileUri(String uri) {
        return uri.matches(".+\\.(html|css|js|ico)");
    }

    public static UriResponse getUriResponse(String uri) throws IOException {
        ControllerManager controllerManager = Arrays.stream(ControllerManager.values())
                .filter(processor -> processor.condition.test(uri))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("처리할 수 없는 요청입니다."));

        return controllerManager.uriProcessor.getResponse(uri);
    }
}

