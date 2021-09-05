package nextstep.jwp.web.network.request;

import nextstep.jwp.web.network.response.ContentType;
import nextstep.jwp.web.network.response.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class FormUrlEncodedHttpBodyTest {

    @DisplayName("FormUrlEncodedHttpBody 객체를 생성한다 - 성공")
    @Test
    void create() {
        // given
        final String rawBody = "account=pobi&password=password&email=pobi%40woowahan.com";

        // when // then
        assertThatCode(() -> FormUrlEncodedHttpBody.of(rawBody))
                .doesNotThrowAnyException();
    }

    @DisplayName("HttpBody에서 쿼리 파라미터를 읽어 map으로 반환한다 - 성공")
    @Test
    void name() {
        // given
        final String rawBody = "account=pobi&password=password&email=pobi%40woowahan.com";

        // when
        final FormUrlEncodedHttpBody body = FormUrlEncodedHttpBody.of(rawBody);
        final String actualAccount = body.getAttribute("account");
        final String actualPassword = body.getAttribute("password");
        final String actualEmail = body.getAttribute("email");

        // then
        assertThat(actualAccount).isEqualTo("pobi");
        assertThat(actualPassword).isEqualTo("password");
        assertThat(actualEmail).isEqualTo("pobi%40woowahan.com");
    }
}