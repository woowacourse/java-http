package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.exception.UncheckedServletException;
import java.util.List;
import org.apache.coyote.http11.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    @DisplayName("헤더 값이 있으면 find 시 present optional 반환")
    @Test
    void find_Present() {
        // given
        HttpHeader httpHeader = new HttpHeader(List.of("Host: localhost:8080", "Connection: keep-alive"));

        // when & then
        assertThat(httpHeader.find("Host")).hasValue("localhost:8080");
    }

    @DisplayName("헤더 값이 없으면 find 시 empty optional 반환")
    @Test
    void find_Empty() {
        // given
        HttpHeader httpHeader = new HttpHeader(List.of("Host: localhost:8080", "Connection: keep-alive"));

        // when & then
        assertThat(httpHeader.find("Hos")).isEmpty();
    }

    @DisplayName("헤더 값이 존재하면 값 조회 가능")
    @Test
    void get_Success() {
        // given
        HttpHeader httpHeader = new HttpHeader(List.of("Host: localhost:8080", "Connection: keep-alive"));

        // when & then
        assertThat(httpHeader.get("Host")).isEqualTo("localhost:8080");
    }

    @DisplayName("헤더 값이 없으면 값 조회 시 예외 발생")
    @Test
    void get_Fail() {
        // given
        HttpHeader httpHeader = new HttpHeader(List.of("Host: localhost:8080", "Connection: keep-alive"));

        // when & then
        assertThatThrownBy(() -> httpHeader.get("Hos"))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("Hos 헤더가 존재하지 않습니다.");
    }

    @DisplayName("헤더 값의 존재 여부 확인 가능")
    @Test
    void contains() {
        // given
        HttpHeader httpHeader = new HttpHeader(List.of("Host: localhost:8080", "Connection: keep-alive"));

        // when & then
        assertAll(
                () -> assertThat(httpHeader.contains("Host")).isTrue(),
                () -> assertThat(httpHeader.contains("Connection")).isTrue(),
                () -> assertThat(httpHeader.contains("aaa")).isFalse()
        );
    }

    @DisplayName("헤더 값 추가 가능")
    @Test
    void add() {
        // given
        HttpHeader httpHeader = new HttpHeader(List.of("Host: localhost:8080", "Connection: keep-alive"));

        // when
        httpHeader.add("Add", "added-value");

        // then
        assertThat(httpHeader.get("Add")).isEqualTo("added-value");
    }

    @DisplayName("헤더 값을 String으로 변환 가능")
    @Test
    void buildMessage() {
        // given
        HttpHeader httpHeader = new HttpHeader(List.of("Host: localhost:8080", "Connection: keep-alive"));

        // when & then
        assertThat(httpHeader.buildMessage()).contains("Host: localhost:8080", "Connection: keep-alive", "\r\n");
    }
}
