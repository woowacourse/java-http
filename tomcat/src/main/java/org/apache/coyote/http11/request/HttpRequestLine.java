package org.apache.coyote.http11.request;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.exception.UncheckedHttpException;
import org.apache.coyote.http11.component.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;

public class HttpRequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int REQUEST_LINE_COUNT = 3;

    private final HttpMethod httpMethod;
    private final HttpRequestUri requestUri;
    private final String httpVersion;

    public HttpRequestLine(String input) {
        validateNotNull(input);
        String[] splitLine = input.split(REQUEST_LINE_DELIMITER);
        validateLineSize(splitLine);
        this.httpMethod = HttpMethod.valueOf(splitLine[0]);
        this.requestUri = HttpRequestUriParser.parse(splitLine[1]);
        this.httpVersion = splitLine[2];
    }

    private void validateNotNull(String input) {
        if (input == null) {
            throw new UncheckedHttpException(new IllegalArgumentException("Http Request Line은 비어있을 수 없습니다."));
        }
    }

    private void validateLineSize(String[] splitLine) {
        if (splitLine.length != REQUEST_LINE_COUNT) {
            throw new UncheckedHttpException(new IllegalArgumentException("Http Request Line 형식이 잘못 되었습니다."));
        }
    }

    public HttpResponse<String> getHttpResponse(Map<String, String> bodies) throws IOException {
        if (httpMethod.isGet()) {
            HttpResponse<?> httpResponse = requestUri.processParams(httpMethod);
            return requestUri.getHttpResponse(httpResponse);
        }
        if (HttpMethod.POST.equals(httpMethod)) {
            HttpResponse<?> httpResponse = requestUri.processParams(httpMethod, bodies);
            return requestUri.getHttpResponse(httpResponse);
        }
        throw new UncheckedHttpException(new UnsupportedOperationException("지원하지 않는 기능입니다."));
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
