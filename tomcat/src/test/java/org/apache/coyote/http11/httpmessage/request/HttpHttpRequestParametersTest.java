package org.apache.coyote.http11.httpmessage.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpHttpRequestParametersTest {

    @Nested
    @DisplayName("파싱 테스트")
    class ParseTest {

        @Test
        @DisplayName("key=value 형식의 문자열을 정상적으로 파싱할 수 있다.")
        void oneKeyValueFromParseTest() {
            //given
            String parameter = "key=value";

            //when
            HttpRequestParameters httpRequestParameters = HttpRequestParameters.parseFrom(parameter);

            //then
            assertThat(httpRequestParameters.getParam("key")).isEqualTo("value");
        }

        @ParameterizedTest
        @DisplayName("key=value 형식의 문자열 여러개를 & 로 이어 붙이면 정상적으로 파싱할 수 있다.")
        @ValueSource(strings = {"key1=value1&key2=value2", "key1=value1&key2=value2&key3=value3"})
        void multipleKeyValueFromParseTest(String parameterText) {
            assertThatCode(() -> HttpRequestParameters.parseFrom(parameterText))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest(name = "{0}을 이용해 문자열을 이어붙이면 제대로 파싱되지 않는다.")
        @ValueSource(strings = {",", "|", ";"})
        void wrongDelimiterTest(String delimiter) {
            //given
            String parameterText = String.join(delimiter, "key1=value1",
                    "key2=value2");

            //when
            HttpRequestParameters httpRequestParameters = HttpRequestParameters.parseFrom(parameterText);

            //then
            assertAll(
                    () -> assertThat(httpRequestParameters.getParam("key1"))
                            .isNotEqualTo("value1"),
                    () -> assertThatThrownBy(() -> httpRequestParameters.getParam("key2"))
                            .isInstanceOf(NoSuchElementException.class)
                            .hasMessage("key2 에 해당하는 값이 존재하지 않습니다.")
            );

        }
    }
}
