package org.apache.coyote.request;

import java.util.regex.Pattern;

public class RequestLine {

    private static final int PART_COUNT = 3;
    private static final Pattern METHOD_TOKEN_PATTERN =
            Pattern.compile("[!#$%&'*+.^_`|~0-9A-Za-z-]+");

    private final Method method;
    private final RequestTarget requestTarget;
    private final String protocol;

    private RequestLine(Method method, RequestTarget requestTarget, String protocol) {
        this.method = method;
        this.requestTarget = requestTarget;
        this.protocol = protocol;
    }

    public static RequestLine from(String rawRequestLine) {
        String[] parts = rawRequestLine.strip().split("\\s+");
        if (parts.length != PART_COUNT || !parts[2].equals("HTTP/1.1")) {
            throw new MalformedRequestException("잘못된 요청 라인입니다: " + rawRequestLine);
        }

        return new RequestLine(
                parseMethod(parts[0]),
                RequestTarget.from(parts[1]),
                parts[2]
        );
    }

    private static Method parseMethod(String method) {
        if (!METHOD_TOKEN_PATTERN.matcher(method).matches()) {
            throw new MalformedRequestException("잘못된 메서드 토큰입니다: " + method);
        }

        try {
            return Method.valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new UnknownMethodException(method, e);
        }
    }

    public Method getMethod() {
        return method;
    }

    public boolean hasMethod(Method method) {
        return this.method == method;
    }

    public String getUri() {
        return String.join("?", requestTarget.getPath(), requestTarget.getQuery());
    }

    public RequestTarget getRequestTarget() {
        return requestTarget;
    }
}
