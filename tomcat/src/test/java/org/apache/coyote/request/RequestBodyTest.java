package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.exception.UncheckedServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RequestBodyTest {

    @DisplayName("생성 성공 - 빈 문자열")
    @Test
    void construct_Success_EmptyString() {
        assertThatCode(() -> new RequestBody(""))
                .doesNotThrowAnyException();
    }

    @DisplayName("생성 성공 - 데이터 1개")
    @Test
    void construct_Success_SingleEntry() {
        RequestBody requestBody = new RequestBody("name=lee");
        assertThat(requestBody.get("name")).isEqualTo("lee");
    }

    @DisplayName("생성 성공 - 데이터 3개")
    @Test
    void construct_Success_TwoEntries() {
        RequestBody requestBody = new RequestBody("name=lee&age=20&nickname=tre");
        assertAll(
                () -> assertThat(requestBody.get("name")).isEqualTo("lee"),
                () -> assertThat(requestBody.get("age")).isEqualTo("20"),
                () -> assertThat(requestBody.get("nickname")).isEqualTo("tre")
        );
    }

    @DisplayName("생성 실패: 올바르지 않은 형식")
    @ParameterizedTest
    @ValueSource(strings = {"name=lee&age20", "name=lee&", "&name=lee", " "})
    void construct_Fail(String rawRequestBody) {
        assertThatThrownBy(() -> new RequestBody(rawRequestBody))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("올바르지 않은 Request Body 형식입니다.");
    }

    @DisplayName("요청 Body가 특정 문자열만을 포함하는지 반환")
    @Test
    void containsAll() {
        // given
        RequestBody requestBody = new RequestBody("name=lee&age=20");

        // when & then
        assertAll(
                () -> assertThat(requestBody.containsExactly("name", "age")).isTrue(),
                () -> assertThat(requestBody.containsExactly("name", "a")).isFalse(),
                () -> assertThat(requestBody.containsExactly("name")).isFalse(),
                () -> assertThat(requestBody.containsExactly("age")).isFalse()
        );
    }

    @DisplayName("요청 Body에서 값 조회")
    @Test
    void get() {
        // given
        RequestBody requestBody = new RequestBody("name=lee&age=20");

        // when & then
        assertAll(
                () -> assertThat(requestBody.get("name")).isEqualTo("lee"),
                () -> assertThat(requestBody.get("age")).isEqualTo("20")
        );
    }
}
