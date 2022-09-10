package org.apache.coyote.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestContentTypeUtilsTest {

    @Test
    @DisplayName("css,js,ico 요청일 경우 고정 자원경로가 맞다.")
    void isDefault() {
        String[] path = {".js", ".css", ".ico"};

        boolean result = Arrays.stream(path)
                .allMatch(RequestContentTypeUtils::isDefault);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("html 요청일 경우 고정 자원경로가 아니다.")
    void isNotDefault() {
        assertThat(RequestContentTypeUtils.isDefault("index.html")).isFalse();
    }

}