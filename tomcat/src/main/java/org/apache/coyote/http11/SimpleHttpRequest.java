package org.apache.coyote.http11;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO : 전체적으로 유효하지 않은 값 파싱에 대한 검증과 예외 처리 및 테스트 보완
public class SimpleHttpRequest implements HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final String requestMessage;

    public SimpleHttpRequest(final String requestMessage) {
        validateConnection(requestMessage);
        this.requestMessage = requestMessage;
    }

    private void validateConnection(final String requestMessage) {
        if (requestMessage == null) {
            throw new IllegalArgumentException("Http 요청 메시지로 null을 입력할 수 없습니다.");
        }
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    @Override
    public HttpMethod getHttpMethod() {
        final int httpMethodIndexNumber = 0;
        final String httpMethodValue = parseRequestLine().split(" ")[httpMethodIndexNumber];
        return HttpMethod.valueOf(httpMethodValue.toUpperCase());
    }

    @Override
    public String getRequestUri() {
        final int requestUriIndexNumber = 1;
        return parseRequestLine().split(" ")[requestUriIndexNumber];
    }

    private String parseRequestLine() {
        return requestMessage.lines()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("유효하지 않은 요청 메시지여서 처리할 수 없습니다."));
    }
}
