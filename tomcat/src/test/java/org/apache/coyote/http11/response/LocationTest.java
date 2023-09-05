package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LocationTest {

    @Test
    @DisplayName("헤더 첫 줄로 Location 객체를 생성할 수 있다")
    void construct() {
        //given
        final String header = "GET / HTTP/1.1";

        //when, then
        assertDoesNotThrow(() -> Location.from(header));
    }

    @Test
    @DisplayName("요청 자원에 확장자가 붙어 있어도 파싱할 수 있다.")
    void construct2() {
        //given
        final String header = "GET /index.html HTTP/1.1";

        //when
        final Location location = Location.from(header);

        //then
        assertThat(location.is("index.html")).isTrue();
    }

    @Test
    @DisplayName("없는 자원으로 생성하면 루트(/) 경로로 지정된다.")
    void construct_notExist() {
        //given
        final String header = "GET /test.html HTTP/1.1";

        //when
        final Location location = Location.from(header);

        //then
        assertThat(location.is("/")).isTrue();
    }
}
