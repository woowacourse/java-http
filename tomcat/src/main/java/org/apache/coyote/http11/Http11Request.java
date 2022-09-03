package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import nextstep.jwp.exception.MethodNotAllowedException;

public class Http11Request {

    private final String requestMethod;
    private final String requestUrl;

    private Http11Request(String requestMethod, String requestUrl) {
        validateRequestMethod(requestMethod);
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
    }

    private static void validateRequestMethod(String requestMethod) {
        if (!"GET".equals(requestMethod)) {
            throw new MethodNotAllowedException(requestMethod + "는 사용할 수 없는 메서드입니다.");
        }
    }

    public static Http11Request of(final InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        final String startLine = bufferedReader.readLine().trim();
        String[] startLineParts = startLine.split(" ");
        return new Http11Request(startLineParts[0], startLineParts[1]);
    }

    public String getRequestUrl() {
        return requestUrl;
    }
}
