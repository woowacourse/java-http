package org.apache.coyote.http11;

public record RequestLineInfo(
        HttpMethod method,
        String path,
        String protocolVersion
) {

    private static final String HEADER_DELIMITER = "\\s+";

    public static RequestLineInfo from(String requestLine) {
        validateRequestLine(requestLine);
        String[] requestParts = requestLine.split(HEADER_DELIMITER);
        validateSplitRequestLine(requestParts);
        String methodToken = requestParts[0].strip();
        String requestUri = requestParts[1].strip();
        String protocolVersion = requestParts[2].strip();
        return new RequestLineInfo(HttpMethod.from(methodToken), requestUri, protocolVersion);
    }

    public static RequestLineInfo of(HttpMethod method, String path, String protocolVersion) {
        return new RequestLineInfo(method, path, protocolVersion);
    }

    private static void validateRequestLine(String requestLine) {
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException("잘못된 요청 라인");
        }
    }

    private static void validateSplitRequestLine(String[] requestParts) {
        if (requestParts.length < 3) {
            throw new IllegalArgumentException("요청 라인 파싱 실패");
        }
    }
}
