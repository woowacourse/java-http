package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class QueryParamsTest {

    @Test
    @DisplayName("빈 객체를 생성한다")
    void empty() {
        QueryParams queryParams = QueryParams.empty();
        assertThat(queryParams.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("쿼리 스트링을 입력받아 key-value 쌍을 파싱한다")
    void parseQueryString() {
        QueryParams queryParams = QueryParams.from("account=gugu&password=password");

        assertAll(
                () -> assertThat(queryParams.get("account")).isEqualTo("gugu"),
                () -> assertThat(queryParams.get("password")).isEqualTo("password")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"account", "account=","account=gugu&password=", "account=gugu=password"})
    @DisplayName("입력받은 쿼리 스트링의 key-value 쌍이 맞지 않으면 예외를 발생시킨다")
    void throwExceptionWhenInvalidQueryString() {
        assertThatThrownBy(() -> QueryParams.from("account=gugu&password="))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("key-value 쌍이 맞지 않습니다");
    }

    @Test
    @DisplayName("query parameter를 가지고 있다면 참을 반환한다")
    void returnTrueIfHasParams() {
        QueryParams queryParams = QueryParams.from("account=gugu");
        assertThat(queryParams.hasParams()).isTrue();
    }

    @Test
    @DisplayName("query parameter를 가지고 있지 않다면 거짓을 반환한다")
    void returnFalseIfHasNoParams() {
        QueryParams queryParams = QueryParams.empty();
        assertThat(queryParams.hasParams()).isFalse();
    }

    @Test
    @DisplayName("가지고 있는 쿼리 파라미터를 조회한다")
    void getKey() {
        QueryParams queryParams = QueryParams.from("account=gugu&password=password");

        String accountValue = queryParams.get("account");
        String passwordValue = queryParams.get("password");

        assertAll(
                () -> assertThat(accountValue).isEqualTo("gugu"),
                () -> assertThat(passwordValue).isEqualTo("password")
        );
    }

    @Test
    @DisplayName("가지고 있지 않은 쿼리 파라미터를 조회할 경우 예외를 발생시킨다")
    void throwExceptionWhenTryToGetKeyThatDoesNotExist() {
        QueryParams queryParams = QueryParams.from("account=gugu");

        assertThatThrownBy(() -> queryParams.get("password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 key 입니다");
    }
}
