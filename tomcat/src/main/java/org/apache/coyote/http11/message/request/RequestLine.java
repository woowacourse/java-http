package org.apache.coyote.http11.message.request;

import org.apache.coyote.http11.message.HttpMethod;

public record RequestLine(
        HttpMethod httpMethod,
        String path,
        String httpVersion
) {
    public static RequestLine createFromRawRequestLine(String rawRequestLine) {
        String[] splitRequestLine = rawRequestLine.split(" ");

        if (splitRequestLine.length < 3) {
            throw new IllegalArgumentException("요청 라인 형식 오류: " + rawRequestLine);
        }

        HttpMethod httpMethod = HttpMethod.findByName(splitRequestLine[0]);
        String path = splitRequestLine[1];
        String httpVersion = splitRequestLine[2];

        return new RequestLine(httpMethod, path, httpVersion);
    }
}
