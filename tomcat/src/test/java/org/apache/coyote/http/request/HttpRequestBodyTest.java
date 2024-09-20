package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.Test;

public class HttpRequestBodyTest {

    @Test
    void 파라미터를_파싱할_수_있다() {
        // given
        String bodyLine = "account=JohnDoe&password=12345";

        // when
        HttpRequestBody httpRequestBody = new HttpRequestBody(bodyLine);

        // then
        assertAll(
                () -> assertThat(httpRequestBody.get("account")).isEqualTo("JohnDoe"),
                () -> assertThat(httpRequestBody.get("password")).isEqualTo("12345")
        );
    }

    @Test
    void 파라미터가_없으면_빈_맵을_반환한다() {
        // given
        String bodyLine = "";

        // when
        HttpRequestBody httpRequestBody = new HttpRequestBody(bodyLine);

        // then
        assertThat(httpRequestBody.getBody()).isEmpty();
    }

    @Test
    void 잘못된_파라미터_형식이_있으면_무시한다() {
        // given
        String bodyLine = "account=JohnDoe&password";

        // when
        HttpRequestBody httpRequestBody = new HttpRequestBody(bodyLine);

        // then
        assertAll(
                () -> assertThat(httpRequestBody.get("account")).isEqualTo("JohnDoe"),
                () -> assertThat(httpRequestBody.get("password")).isNull()
        );
    }

    @Test
    void 파라미터가_여러개일_때_모두_파싱할_수_있다() {
        // given
        String bodyLine = "account=JohnDoe&password=12345&email=johndoe@example.com";

        // when
        HttpRequestBody httpRequestBody = new HttpRequestBody(bodyLine);

        // then
        Map<String, String> bodyMap = httpRequestBody.getBody();
        assertAll(
                () -> assertThat(bodyMap.size()).isEqualTo(3),
                () -> assertThat(bodyMap.get("account")).isEqualTo("JohnDoe"),
                () -> assertThat(bodyMap.get("password")).isEqualTo("12345"),
                () -> assertThat(bodyMap.get("email")).isEqualTo("johndoe@example.com")
        );
    }

    @Test
    void 값이_없는_파라미터는_무시한다() {
        // given
        String bodyLine = "account=JohnDoe&password=&email=johndoe@example.com";

        // when
        HttpRequestBody httpRequestBody = new HttpRequestBody(bodyLine);

        // then
        assertAll(
                () -> assertThat(httpRequestBody.get("password")).isEmpty(),
                () -> assertThat(httpRequestBody.get("account")).isEqualTo("JohnDoe"),
                () -> assertThat(httpRequestBody.get("email")).isEqualTo("johndoe@example.com")
        );
    }
}
