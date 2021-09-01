package nextstep.jwp.web.network.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class HttpBodyTest {

    @DisplayName("HttpBody 객체를 생성한다 - 성공")
    @Test
    void create() {
        // given
        final byte[] httpBodyAsByte = "this is body".getBytes();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(httpBodyAsByte)));

        // when // then
        assertThatCode(() -> HttpBody.of(bufferedReader, httpBodyAsByte.length))
                .doesNotThrowAnyException();
    }

    @DisplayName("HttpBody에서 쿼리 파라미터를 읽어 map으로 반환한다 - 성공")
    @Test
    void name() {
        // given
        final byte[] httpBodyAsByte = "account=pobi&password=password&email=pobi%40woowahan.com".getBytes();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(httpBodyAsByte)));
        final HttpBody body = HttpBody.of(bufferedReader, httpBodyAsByte.length);

        // when
        final Map<String, String> bodyAsMap = body.asMap();

        // then
        assertThat(bodyAsMap)
                .containsEntry("account", "pobi")
                .containsEntry("password", "password")
                .containsEntry("email", "pobi%40woowahan.com");
    }
}