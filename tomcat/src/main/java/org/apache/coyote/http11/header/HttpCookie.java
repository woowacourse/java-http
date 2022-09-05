package org.apache.coyote.http11.header;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.coyote.http11.request.headers.RequestHeader;
import org.apache.coyote.http11.response.headers.ResponseHeader;

public class HttpCookie implements RequestHeader, ResponseHeader {

    private static final String DELIMITER = "; ";
    private static final Pattern PATTERN = Pattern.compile("(?<key>.+)=(?<value>.+)");

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie parse(String ignored, String value) {

        return new HttpCookie(Arrays.stream(value.split(";"))
                .map(String::trim)
                .map(PATTERN::matcher)
                .filter(Matcher::find)
                .collect(Collectors.toMap(
                        matcher -> matcher.group("key"),
                        matcher -> matcher.group("value")
                )));
    }

    @Override
    public String getField() {
        return "Cookie";
    }

    @Override
    public String getValue() {
        return String.join(DELIMITER, cookies.values());
    }

    @Override
    public String getAsString() {
        return getField() + ": " + getValue();
    }
}
