package com.techcourse.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BodyParserTest {

    @DisplayName("body로 들어온 키-값 쌍을 파싱한다")
    @Test
    void parseValues() {
        // given
        String body = "account=로빈&password=password&email=robin@naver.com";

        // when
        Map<String, String> actual = BodyParser.parseValues(body);

        // then
        Map<String, String> expected = Map.of(
                "account", "로빈",
                "password", "password",
                "email", "robin@naver.com"
        );
        assertThat(actual)
                .isEqualTo(expected);
    }
}
