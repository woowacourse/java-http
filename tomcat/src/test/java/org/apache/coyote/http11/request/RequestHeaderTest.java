package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeaderTest {

    @DisplayName("Request Header를 파싱한다")
    @Test
    void parse() {
        final List<String> lines = List.of(
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=123lk1j2v6lh5v6846 ",
                "Connection: keep-alive "
        );

        final RequestHeader actual = RequestHeader.parse(lines);

        assertAll(
                () -> assertThat(actual.find("Host")).hasValue("localhost:8080"),
                () -> assertThat(actual.find("Cookie")).hasValue("JSESSIONID=123lk1j2v6lh5v6846"),
                () -> assertThat(actual.find("Connection")).hasValue("keep-alive")
        );
    }

    @DisplayName("존재하지 않는 Header 값을 조회한다")
    @Test
    void findEmpty() {
        final List<String> lines = List.of("Host: localhost:8080 ");
        final RequestHeader actual = RequestHeader.parse(lines);

        assertThat(actual.find("Cookie")).isEmpty();
    }
}