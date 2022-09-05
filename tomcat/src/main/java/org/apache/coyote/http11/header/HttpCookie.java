package org.apache.coyote.http11.header;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.coyote.http11.request.headers.RequestHeader;
import org.apache.coyote.http11.response.PostProcessMeta;
import org.apache.coyote.http11.response.headers.ResponseHeader;

public class HttpCookie implements RequestHeader, ResponseHeader {

    private static final String DELIMITER = "; ";
    private static final Pattern PATTERN = Pattern.compile("(?<key>.+)=(?<value>.+)");
    private static final String SESSION_ID = "JSESSIONID";

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static ResponseHeader empty() {
        return new HttpCookie(new HashMap<>());
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
        return cookies.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(DELIMITER));
    }

    @Override
    public ResponseHeader postProcess(PostProcessMeta meta) {
        Map<String, String> newCookies = new HashMap<>(cookies);
        Optional<RequestHeader> optionalHeader = meta.findHeaderByField(getField());
        if (optionalHeader.isEmpty() || !isSessionIdExists(optionalHeader.get())) {
            newCookies.put(SESSION_ID, UUID.randomUUID().toString());
        }
        return new HttpCookie(newCookies);
    }

    private static boolean isSessionIdExists(RequestHeader header) {
        return header.getValue().contains(SESSION_ID);
    }

    @Override
    public String getAsString() {
        String setCookie = cookies.get(SESSION_ID);
        if (setCookie == null) {
            return "";
        }
        return "Set-Cookie: JSESSIONID=" + setCookie;
    }
}
