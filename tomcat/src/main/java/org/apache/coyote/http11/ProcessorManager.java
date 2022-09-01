package org.apache.coyote.http11;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;
import org.apache.coyote.http11.urlprocessor.FileUrlProcessor;
import org.apache.coyote.http11.urlprocessor.LoginUrlProcessor;
import org.apache.coyote.http11.urlprocessor.RootUrlProcessor;
import org.apache.coyote.http11.urlprocessor.UrlProcessor;

public enum ProcessorManager {
    ROOT(ProcessorManager::isRootUrl, new RootUrlProcessor()),
    LOGIN(ProcessorManager::isLoginUrl, new LoginUrlProcessor()),
    FILE(ProcessorManager::isFileUrl, new FileUrlProcessor());

    private final Predicate<String> condition;

    private final UrlProcessor urlProcessor;

    ProcessorManager(Predicate<String> condition, UrlProcessor urlProcessor) {
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

    public static UrlResponse getUrlResponse(String url) throws IOException {
        ProcessorManager processorManager = Arrays.stream(ProcessorManager.values())
                .filter(processor -> processor.condition.test(url))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("처리할 수 없는 요청입니다."));

        return processorManager.urlProcessor.getResponse(url);
    }
}

