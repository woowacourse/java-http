package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BodyParserTest {

    @DisplayName("요청, 응답의 Body 부분을 반환한다.")
    @Test
    void parseTest() {
        String body = "username=hi&password=1234";

        Map<String, String> result = BodyParser.parse(body);

        assertThat(result).isEqualTo(Map.of("username", "hi", "password", "1234"));
    }

    @DisplayName("파라미터의 value 가 한개도 없으면 빈 문자로 대체한다.")
    @Test
    void parseTest2() {
        String body = "username=hi&password=";

        Map<String, String> result = BodyParser.parse(body);

        assertThat(result).isEqualTo(Map.of("username", "hi", "password", ""));
    }

    @DisplayName("= 으로 구분되는 값이 여러개면 첫 번째 value를 반환한다.")
    @Test
    void parseTest3() {
        String body = "username=hi&password=1234=5678";

        Map<String, String> result = BodyParser.parse(body);

        assertThat(result).isEqualTo(Map.of("username", "hi", "password", "1234"));
    }
}
