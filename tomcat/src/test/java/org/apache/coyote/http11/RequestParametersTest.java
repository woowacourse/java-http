package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestParametersTest {

    @Test
    @DisplayName("키와 값 쌍을 파싱한다")
    void parse() {
        RequestParameters parameters = RequestParameters.from("account=gugu&password=1234");

        assertThat(parameters.get("account")).contains("gugu");
        assertThat(parameters.get("password")).contains("1234");
    }

    @Test
    @DisplayName("파라미터가 하나면 그것만 파싱한다")
    void singleParameter() {
        RequestParameters parameters = RequestParameters.from("account=gugu");

        assertThat(parameters.get("account")).contains("gugu");
        assertThat(parameters.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("값에 등호가 포함되어도 첫 번째 등호만 기준으로 나눈다")
    void valueWithEqualSign() {
        RequestParameters parameters = RequestParameters.from("token=YWJjZA==");

        assertThat(parameters.get("token")).contains("YWJjZA==");
    }

    @Test
    @DisplayName("존재하지 않는 키를 조회하면 빈 값을 반환한다")
    void notFound() {
        RequestParameters parameters = RequestParameters.from("account=gugu");

        assertThat(parameters.get("email")).isEmpty();
    }

    @Test
    @DisplayName("빈 문자열이면 빈 파라미터를 반환한다")
    void emptyString() {
        RequestParameters parameters = RequestParameters.from("");

        assertThat(parameters.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("null이면 빈 파라미터를 반환한다")
    void nullInput() {
        RequestParameters parameters = RequestParameters.from(null);

        assertThat(parameters.isEmpty()).isTrue();
    }
}
