package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nextstep.jwp.exception.NotFoundException;

public enum Url {

    DEFAULT("/"::equals, url -> new Http11Response("html", "Hello world!")),
    LOGIN(url -> isMatchRegex("/login" + ".*", url), url -> Http11Response.from("/login.html")),
    RESOURCE(url -> isMatchRegex(".*" + "\\." + ".*", url), Http11Response::from);


    private final Predicate<String> condition;
    private final Function<String, Http11Response> resourcePath;

    Url(Predicate<String> condition, Function<String, Http11Response> resourcePath) {
        this.condition = condition;
        this.resourcePath = resourcePath;
    }

    public static Http11Response getResponseFrom(String url) {
        return Arrays.stream(values())
                .filter(value -> value.condition.test(url))
                .map(value -> value.resourcePath.apply(url))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("존재하지 않는 페이지입니다."));
    }

    private static boolean isMatchRegex(String regex, String url) {
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(url);
        return matcher.find();
    }
}
