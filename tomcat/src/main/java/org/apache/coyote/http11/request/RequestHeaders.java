package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.coyote.http11.request.headers.RequestHeader;
import org.apache.coyote.http11.request.headers.RequestHeaderMapper;


public class RequestHeaders {

    private static final Pattern HEADER_PATTERN = Pattern.compile("(?<field>[a-zA-Z\\- ]+): ?(?<value>.+)");

    private final Map<String, RequestHeader> headers;

    private RequestHeaders(Map<String, RequestHeader> headers) {
        this.headers = headers;
    }

    public static RequestHeaders parse(List<String> lines) {
        return new RequestHeaders(lines.stream()
                .map(HEADER_PATTERN::matcher)
                .filter(Matcher::find)
                .collect(Collectors.toMap(
                        matcher -> matcher.group("field"),
                        matcher -> RequestHeaderMapper.findAndApply(matcher.group("field"), matcher.group("value"))
                )));
    }

    public RequestHeader findHeader(String field) {
        return headers.get(field);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("\n");
        for (Entry<String, RequestHeader> entry : headers.entrySet()) {
            builder.append(entry.getKey()).append(" -> ").append(entry.getValue());
        }
        return "RequestHeaders{\n" +
                "headers={" + builder +
                "\n}\n}";
    }
}
