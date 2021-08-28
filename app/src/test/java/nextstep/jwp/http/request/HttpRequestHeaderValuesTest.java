package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;

class HttpRequestHeaderValuesTest {

    private static final String VALUE1 = "header";
    private static final String VALUE2 = "yaho";
    private static final String VALUE3 = "wedge";

    @DisplayName("공백이나 null이 오면 예외가 발생하는지 확인")
    @ParameterizedTest
    @NullSource
    @EmptySource
    void whenValueIsNullOrEmpty(String nonValidValue) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new HttpRequestHeaderValues(VALUE1, VALUE2, nonValidValue))
            .hasMessageContaining("HttpRequestHeader의 value로 null 혹은 공백이 올 수 없습니다.");
    }

    @DisplayName("리퀘스트 헤더 값 생성 테스트")
    @Test
    void createTest() {
        //given
        //when
        HttpRequestHeaderValues httpRequestHeaderValues = new HttpRequestHeaderValues(VALUE1,
            VALUE2, VALUE3);
        //then
        assertThat(httpRequestHeaderValues).isNotNull();
    }

    @DisplayName("해더 값 문자열 반환 테스트")
    @Test
    void toValuesStringTest() {
        //given
        //when
        HttpRequestHeaderValues httpRequestHeaderValues = new HttpRequestHeaderValues(VALUE1,
            VALUE2, VALUE3);
        String valuesString = httpRequestHeaderValues.toValuesString();
        //then
        String expectedResult = VALUE1 + "," + VALUE2 + "," + VALUE3;
        assertThat(valuesString).isEqualTo(expectedResult);
    }

    @DisplayName("콤마가 포함되어있으면 예외")
    @Test
    void whenKeyContainsComma() {
        //given
        //when
        //then
        assertThatThrownBy(() -> new HttpRequestHeaderValues(VALUE1, VALUE2 + ","))
            .hasMessageContaining("HttpRequestHeader의 value에 \",\"가 포함될 수 없습니다.");
    }
}