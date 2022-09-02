package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PathTest {

    @DisplayName("쿼리파라미터가 없는 요청일경우의 Path 반환")
    @Test
    void from() {
        final String uri = "/index.html";
        final String expectedResource = "/index.html";
        final String expectedContentType = "text/html";

        Path path = Path.from(uri);

        assertAll(
                () -> assertThat(path.getResource()).isEqualTo(expectedResource),
                () -> assertThat(path.getContentType()).isEqualTo(expectedContentType),
                () -> assertThat(path.getQueryParameter()).isEmpty()
        );
    }

    @DisplayName("쿼리파라미터가 있는 요쳥일 경우의 Path 반환")
    @Test
    void from_With_QueryParam() {
        final String uri = "/index.html?username=east&password=password";
        final String expectedResource = "/index.html";
        final String expectedContentType = "text/html";
        final Map<String, String> expectedParams = new HashMap<>();
        expectedParams.put("username", "east");
        expectedParams.put("password", "password");

        Path path = Path.from(uri);

        assertAll(
                () -> assertThat(path.getResource()).isEqualTo(expectedResource),
                () -> assertThat(path.getContentType()).isEqualTo(expectedContentType),
                () -> assertThat(path.getQueryParameter()).isEqualTo(expectedParams)
        );
    }
}
