package org.apache.coyote.http11;

public record RequestLine(String method, String path, String protocolVersion) {

    public static RequestLine parse(final String rawRequestLine) {
        if (rawRequestLine == null) {
            return null;
        }

        final String[] parsedRequestLine = rawRequestLine.split("\\s+", 3);

        //TODO: 유효성 검사 로직 후 예외 발생 -> 400 등의 상태 코드 처리
        final String method = parsedRequestLine[0].trim();
        final String path = parsedRequestLine[1].trim();
        final String protocolVersion = parsedRequestLine[2].trim();

        return new RequestLine(method, path, protocolVersion);
    }
}
