package org.apache.coyote.http11.utils;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public enum ResourceMatcher implements Predicate<String> {

    DEFAULT(url -> url.endsWith("/"), url -> url += "hello.txt"),
    HTML(url -> !url.contains("."), url -> url += ".html"),
    ;

    private final Predicate<String> predicate;
    private final Function<String, String> resourceName;

    ResourceMatcher(final Predicate<String> predicate,
                    final Function<String, String> resourceName) {
        this.predicate = predicate;
        this.resourceName = resourceName;
    }

    public static String matchName(String url) {
        return Arrays.stream(ResourceMatcher.values())
                .filter(type -> type.test(url))
                .findFirst()
                .map(resourceMatcher -> resourceMatcher.match(url))
                .orElse(url);
    }

    public String match(String url) {
        return this.resourceName.apply(url);
    }

    @Override
    public boolean test(final String url) {
        return predicate.test(url);
    }
}
