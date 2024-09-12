package org.apache.coyote.http11.request.header;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @DisplayName("헤더 필드 리스트를 헤더 이름 - 값으로 파싱하여 객체를 생성한다.")
    @Test
    void createRequestHeadersFromHeaderFields() {
        List<String> headerFields = List.of(
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 80",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*"
        );

        RequestHeaders requestHeaders = new RequestHeaders(headerFields);

        assertAll(
                () -> assertThat(requestHeaders.get("Host")).isPresent().hasValue("localhost:8080"),
                () -> assertThat(requestHeaders.get("Connection")).isPresent().hasValue("keep-alive"),
                () -> assertThat(requestHeaders.get("Content-Length")).isPresent().hasValue("80"),
                () -> assertThat(requestHeaders.get("Content-Type")).isPresent()
                        .hasValue("application/x-www-form-urlencoded"),
                () -> assertThat(requestHeaders.get("Accept")).isPresent().hasValue("*/*")
        );
    }
}
