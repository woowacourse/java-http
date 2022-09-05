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

    public String getValueByField(String field) {
        return headers.stream()
                .filter(header -> header.getField().equals(field))
                .map(RequestHeader::getValue)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("reuqest header 에 " + field + " 가 존재하지 않습니다."));
    }

    public String getPairByField(String field) {
        return field + ": " + getValueByField(field);
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
