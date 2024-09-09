package org.apache.coyote.http11.request.body.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FormUrlEncodedParserTest {

    @DisplayName("바디 파라미터를 이름 - 값으로 파싱하여 맵을 반환한다.")
    @Test
    void parseQueryParameters() {
        String body = "name=jazz&password=password&age=24";

        BodyParser parser = new FormUrlEncodedParser();
        Map<String, String> params = parser.parse(body);

        assertAll(
                () -> assertThat(params.get("name")).isEqualTo("jazz"),
                () -> assertThat(params.get("password")).isEqualTo("password"),
                () -> assertThat(params.get("age")).isEqualTo("24"));
    }
}
