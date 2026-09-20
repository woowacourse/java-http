package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestBodyTest {

    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    @Test
    @DisplayName("form-urlencoded 바디는 key로 값을 조회할 수 있다.")
    void parseFormUrlEncoded() {
        // given
        RequestBody requestBody = new RequestBody("account=gugu&password=password", FORM_URLENCODED);

        // when
        String account = requestBody.getValue("account");
        String password = requestBody.getValue("password");

        // then
        assertThat(account).isEqualTo("gugu");
        assertThat(password).isEqualTo("password");
    }

    @Test
    @DisplayName("퍼센트 인코딩된 값은 디코딩되어 조회된다.")
    void decodePercentEncodedValue() {
        // given
        RequestBody requestBody = new RequestBody("email=gugu%40woowahan.com", FORM_URLENCODED);

        // when
        String email = requestBody.getValue("email");

        // then
        assertThat(email).isEqualTo("gugu@woowahan.com");
    }

    @Test
    @DisplayName("Content-Type이 없으면 form-urlencoded로 간주한다.")
    void nullContentTypeTreatedAsFormUrlEncoded() {
        // given
        RequestBody requestBody = new RequestBody("account=gugu", null);

        // when
        String account = requestBody.getValue("account");

        // then
        assertThat(account).isEqualTo("gugu");
    }

    @Test
    @DisplayName("존재하지 않는 key로 조회하면 null을 반환한다.")
    void invalidKeyReturnNull() {
        // given
        RequestBody requestBody = new RequestBody("account=gugu", FORM_URLENCODED);

        // when
        String actual = requestBody.getValue("invalidKey");

        // then
        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("빈 바디는 어떤 key로 조회해도 null을 반환한다.")
    void emptyBodyReturnNull() {
        // given
        RequestBody requestBody = new RequestBody("", FORM_URLENCODED);

        // when
        String actual = requestBody.getValue("account");

        // then
        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("지원하지 않는 Content-Type이면 예외가 발생한다.")
    void unsupportedContentTypeThrows() {
        // given
        RequestBody requestBody = new RequestBody("{\"account\":\"gugu\"}", "application/json");

        // when & then
        assertThatThrownBy(() -> requestBody.getValue("account"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 Content-Type");
    }

}
