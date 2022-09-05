package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.HttpRequestBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestBodyTest {

    @Test
    @DisplayName("Body 값을 파싱해서 HttpRequestBody를 생성한다.")
    void parseRequestBody() {
        // given
        final String requestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";

        // when
        final HttpRequestBody httpRequestBody = new HttpRequestBody(requestBody);

        // then
        assertAll(
                () -> assertThat(httpRequestBody.getBodyValue("account")).isEqualTo("gugu"),
                () -> assertThat(httpRequestBody.getBodyValue("password")).isEqualTo("password"),
                () -> assertThat(httpRequestBody.getBodyValue("email")).isEqualTo("hkkang%40woowahan.com")
        );
    }
}
