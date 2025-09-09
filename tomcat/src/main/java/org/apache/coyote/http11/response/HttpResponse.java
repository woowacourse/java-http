package org.apache.coyote.http11.response;

import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.MimeType;

import java.nio.charset.StandardCharsets;
import java.util.Map;

// 임시 클래스
public record HttpResponse(HttpStatus httpStatus, String body, MimeType mimeType, Map<String, String> headers) {

    public String toHttpResponse() {
        // StringBuilder를 사용하여 효율적으로 문자열을 만듭니다.
        StringBuilder builder = new StringBuilder();

        // 1. 상태 라인 추가
        builder.append("HTTP/1.1 ").append(httpStatus.getPhrase()).append("\r\n");

        // 2. 필수 헤더 추가 (Content-Type, Content-Length)
        builder.append("Content-Type: ").append(mimeType.getMimeType()).append("\r\n");
        builder.append("Content-Length: ").append(body.getBytes(StandardCharsets.UTF_8).length).append("\r\n");

        // 3. 커스텀 헤더 추가
        for (Map.Entry<String, String> header : headers.entrySet()) {
            builder.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }

        // 4. 헤더와 바디를 구분하는 빈 줄 추가
        builder.append("\r\n");

        // 5. 바디 추가
        builder.append(body);

        return builder.toString();
    }
}
