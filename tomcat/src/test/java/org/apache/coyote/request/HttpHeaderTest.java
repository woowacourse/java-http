package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.exception.UncheckedServletException;
import java.util.List;
import org.apache.coyote.http11.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class HttpHeaderTest {

    @DisplayName("생성 성공")
    @ParameterizedTest
    @ValueSource(strings = {"Connection: keep-alive", "Connection:keep-alive", "Connection:  keep-alive  "})
    void construct_Success(String validHeader) {
        List<String> headers = List.of("Host: localhost:8080", validHeader);
        HttpHeader httpHeader = new HttpHeader(headers);
        assertAll(
                () -> assertThat(httpHeader.get("Host")).isEqualTo("localhost:8080"),
                () -> assertThat(httpHeader.get("Connection")).isEqualTo("keep-alive")
        );
    }

    @DisplayName("생성 실패: 올바르지 않은 헤더 포함")
    @ParameterizedTest
    @ValueSource(strings = {"NoColon", ": StartWithColon"})
    @EmptySource
    void construct_Fail(String invalidHeader) {
        List<String> headers = List.of("Host: localhost:8080", invalidHeader);
        assertThatThrownBy(() -> new HttpHeader(headers))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("형식이 올바르지 않은 헤더가 포함되어 있습니다.");
    }
}
