package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.UncheckedServletException;
import org.junit.jupiter.api.Test;

class RequestStartLineTest {

    private static final String DEFAULT_METHOD = "GET";
    private static final String DEFAULT_PATH = "/";

    @Test
    void validateTest_whenMatchProtocolVersion() {
        String protocolVersion = "HTTP/1.1";

        assertThatCode(() -> new RequestStartLine(DEFAULT_METHOD, DEFAULT_PATH, protocolVersion))
                .doesNotThrowAnyException();
    }


    @Test
    void validateTest_whenNotMatchProtocolVersion_throwException() {
        String protocolVersion = "HTTP/2";

        assertThatThrownBy(() -> new RequestStartLine(DEFAULT_METHOD, DEFAULT_PATH, protocolVersion))
                .isExactlyInstanceOf(UncheckedServletException.class)
                .hasMessage("해당 버전의 프로토콜을 지원하지 않습니다.");
    }
}
