package org.apache.coyote.http11.request;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.coyote.http11.request.headers.RequestHeader;
import org.apache.coyote.http11.request.headers.RequestHeaderMapper;


public class RequestHeaders {

    private static final Pattern HEADER_PATTERN = Pattern.compile("(?<field>[a-zA-Z\\- ]+): ?(?<value>.+)");

    private final List<RequestHeader> headers;

    private RequestHeaders(List<RequestHeader> headers) {
        this.headers = headers;
    }

    public static RequestHeaders parse(List<String> lines) {
        return new RequestHeaders(lines.stream()
                .map(HEADER_PATTERN::matcher)
                .filter(Matcher::find)
                .map(matcher -> RequestHeaderMapper.findAndApply(matcher.group("field"), matcher.group("value")))
                .collect(Collectors.toList()));
    }

    public String findValueByField(String field) {
        return getHeader(field).getValue();
    }

    public String findPairByField(String field) {
        return field + ": " + findValueByField(field);
    }

    public RequestHeader getHeader(String field) {
        return headers.stream()
                .filter(header -> header.getField().equals(field))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("no " + field));
    }

    public List<RequestHeader> getHeaders() {
        return List.copyOf(headers);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("\n");
        for (RequestHeader header : headers) {
            builder.append(header.getField()).append(" -> ").append(header.getValue());
        }
        return "RequestHeaders{\n" +
                "headers={" + builder +
                "\n}\n}";
    }
}
