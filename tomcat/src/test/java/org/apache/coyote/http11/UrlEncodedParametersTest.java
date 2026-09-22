package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("URL 인코딩된 파라미터")
class UrlEncodedParametersTest {

    @Nested
    @DisplayName("정상 파싱")
    class ValidParsing {

        @Test
        @DisplayName("퍼센트 인코딩된 값을 디코딩한다")
        void decodesPercentEncodedValue() {
            // given
            final var encodedParameters = "email=hkkang%40woowahan.com";

            // when
            final var parameters = UrlEncodedParameters.parse(encodedParameters).orElseThrow();

            // then
            assertThat(parameters.get("email")).contains("hkkang@woowahan.com");
        }

        @Test
        @DisplayName("더하기 기호를 공백으로 디코딩한다")
        void decodesPlusAsSpace() {
            // given
            final var encodedParameters = "name=woowa+course";

            // when
            final var parameters = UrlEncodedParameters.parse(encodedParameters).orElseThrow();

            // then
            assertThat(parameters.get("name")).contains("woowa course");
        }

        @Test
        @DisplayName("값에 포함된 등호를 보존한다")
        void preservesEqualsSignInValue() {
            // given
            final var encodedParameters = "note=a=b";

            // when
            final var parameters = UrlEncodedParameters.parse(encodedParameters).orElseThrow();

            // then
            assertThat(parameters.get("note")).contains("a=b");
        }

        @Test
        @DisplayName("값에 포함된 인코딩된 앰퍼샌드를 보존한다")
        void preservesEncodedAmpersandInValue() {
            // given
            final var encodedParameters = "note=a%26b";

            // when
            final var parameters = UrlEncodedParameters.parse(encodedParameters).orElseThrow();

            // then
            assertThat(parameters.get("note")).contains("a&b");
        }
    }

    @Nested
    @DisplayName("잘못된 파라미터")
    class InvalidParameters {

        @Test
        @DisplayName("이름과 값이 구분되지 않으면 파싱하지 않는다")
        void rejectsParameterWithoutValue() {
            // given
            final var encodedParameters = "account";

            // when
            final var parameters = UrlEncodedParameters.parse(encodedParameters);

            // then
            assertThat(parameters).isEmpty();
        }

        @Test
        @DisplayName("잘못된 퍼센트 인코딩이면 파싱하지 않는다")
        void rejectsInvalidPercentEncoding() {
            // given
            final var encodedParameters = "account=%ZZ";

            // when
            final var parameters = UrlEncodedParameters.parse(encodedParameters);

            // then
            assertThat(parameters).isEmpty();
        }
    }
}
