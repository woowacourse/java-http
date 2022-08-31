package org.apache.coyote.http11;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;
import org.apache.coyote.http11.urlprocessor.FileUrlProcessor;
import org.apache.coyote.http11.urlprocessor.LoginUrlProcessor;
import org.apache.coyote.http11.urlprocessor.RootUrlProcessor;
import org.apache.coyote.http11.urlprocessor.UrlProcessor;

public enum Processors {
    ROOT(Processors::isRootUrl, new RootUrlProcessor()),
    LOGIN(Processors::isLoginUrl, new LoginUrlProcessor()),
    FILE(Processors::isFileUrl, new FileUrlProcessor());

    private final Predicate<String> condition;

    private final UrlProcessor urlProcessor;

    Processors(Predicate<String> condition, UrlProcessor urlProcessor) {
        this.condition = condition;
        this.urlProcessor = urlProcessor;
    }

    private static boolean isRootUrl(String url) {
        return url.equals("/");
    }

    private static boolean isLoginUrl(String url) {
        return url.startsWith("/login?");
    }

    private static boolean isFileUrl(String url) {
        return url.matches(".+\\.(html|css|js|ico)");
    }

    public static Processors of(String url) {
        return Arrays.stream(Processors.values())
                .filter(processors -> processors.condition.test(url))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("처리할 수 없는 요청입니다."));
    }

    public UrlResponse getUrlResponse(String url) throws IOException {
        return urlProcessor.getResponse(url);
    }
}

