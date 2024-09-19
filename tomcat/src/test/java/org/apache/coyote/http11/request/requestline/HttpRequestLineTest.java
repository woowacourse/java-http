package org.apache.coyote.http11.request.requestline;

import static support.FakeRequests.invalidGetRequest;
import static support.FakeRequests.validGetRequest;

import org.apache.coyote.http11.request.requestline.HttpRequestLine;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestLineTest {
    @Test
    @DisplayName("유효하지 않은 request line이라면 예외가 발생한다")
    void invalidRequestLine() {
        Assertions.assertThatCode(() -> HttpRequestLine.from(validGetRequest))
                .doesNotThrowAnyException();
        Assertions.assertThatThrownBy(() -> HttpRequestLine.from(invalidGetRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
