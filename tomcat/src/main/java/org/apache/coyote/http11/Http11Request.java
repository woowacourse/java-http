package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import nextstep.jwp.exception.MethodNotAllowedException;

public class Http11Request {

    private static final List<String> ALLOWED_METHODS = List.of("GET", "POST");
    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_URL_INDEX = 1;

    private final String requestMethod;
    private final String requestUrl;

    private Http11Request(String requestMethod, String requestUrl) {
        validateRequestMethod(requestMethod);
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
    }

    private void validateRequestMethod(String requestMethod) {
        if (!ALLOWED_METHODS.contains(requestMethod)) {
            throw new MethodNotAllowedException(requestMethod + "는 사용할 수 없는 메서드입니다.");
        }
    }

    public static Http11Request of(final InputStream inputStream) throws IOException {
        final String startLine = getStartLine(inputStream);
        final String[] startLineParts = startLine.split(" ");
        validateUrl(startLineParts);
        return new Http11Request(startLineParts[REQUEST_METHOD_INDEX], startLineParts[REQUEST_URL_INDEX]);
    }

    private static String getStartLine(InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        return bufferedReader.readLine().trim();
    }

    private static void validateUrl(String[] startLineParts) {
        if (startLineParts[REQUEST_URL_INDEX] == null) {
            throw new IllegalArgumentException("잘못된 형식의 요청입니다.");
        }
    }

    public String getRequestUrl() {
        return requestUrl;
    }
}
