package org.apache.coyote.http11.domain.header;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeaderTest {

    @Test
    @DisplayName("Content 가 존재하지 않을 경우 Content-Length 값을 0으로 반환한다.")
    void getBodyLength_WhenContentIsNull() {
        List<String> headers = List.of("Host: localhost:8080", "Connection: keep-alive", "Accept: */*");
        RequestHeader requestHeader = new RequestHeader(headers);

        assertEquals(requestHeader.getBodyLength(), 0);
    }

    @Test
    @DisplayName("헤더의 Content-Length의 값을 반환환다.")
    void getBodyLength_WhenContentExist() {
        List<String> headers = List.of("Host: localhost:8080", "Connection: keep-alive", "Accept: */*",
                "Content-Length: 80", "Content-Type: application/x-www-form-urlencoded");
        RequestHeader requestHeader = new RequestHeader(headers);

        assertEquals(requestHeader.getBodyLength(), 80);
    }
}
