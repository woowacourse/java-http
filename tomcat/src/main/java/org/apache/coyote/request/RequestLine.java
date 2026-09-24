package org.apache.coyote.request;

public class RequestLine {

    private static final int PART_COUNT = 3;

    private final Method method;
    private final RequestTarget requestTarget;
    private final String protocol;

    private RequestLine(Method method, RequestTarget requestTarget, String protocol) {
        this.method = method;
        this.requestTarget = requestTarget;
        this.protocol = protocol;
    }

    public static RequestLine from(String rawRequestLine) {
        String[] parts = rawRequestLine.strip().split("\\s+", PART_COUNT);
        if (parts.length < PART_COUNT) {
            throw new IllegalArgumentException(
                    String.format("요청 라인은 %d개의 파트로 이루어져야 합니다. '%s'의 파트 수는 %d개입니다.",
                            PART_COUNT, rawRequestLine, parts.length)
            );
        }

        return new RequestLine(
                Method.valueOf(parts[0]),
                RequestTarget.from(parts[1]),
                parts[2]
        );
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
