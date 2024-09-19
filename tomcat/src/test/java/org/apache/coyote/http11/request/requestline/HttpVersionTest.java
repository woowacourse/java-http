package org.apache.coyote.http11.request.requestline;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpVersionTest {

    @DisplayName("지원하지 않는 http 버전이라면 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(strings = {"HTTP/0.8","HTTP/1.2"})
    void from(String data) {
        Assertions.assertThatThrownBy(() -> HttpVersion.from(data)).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("지원하는 http 버전이라면 객체가 반환된다")
    @ParameterizedTest
    @ValueSource(strings = {"HTTP/0.9","HTTP/1.1"})
    void from2(String data) {
        Assertions.assertThat(HttpVersion.from(data)).isInstanceOf(HttpVersion.class);
    }
}
