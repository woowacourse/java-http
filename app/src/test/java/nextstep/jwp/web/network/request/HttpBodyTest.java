package nextstep.jwp.web.network.request;

import nextstep.jwp.web.network.response.ContentType;
import nextstep.jwp.web.network.response.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThatCode;

class HttpBodyTest {

    @DisplayName("HttpBody 객체를 생성한다 - 성공")
    @Test
    void create() {
        // given
        final byte[] httpBodyAsByte = "account=pobi&password=password&email=pobi%40woowahan.com".getBytes();
        final byte[] httpHeadersAsByte = String.format("Content-Type: %s \r\nContent-Length: %d", ContentType.FORM, httpBodyAsByte.length).getBytes();
        final BufferedReader headersBufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(httpHeadersAsByte)));
        final BufferedReader bodyBufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(httpBodyAsByte)));
        final HttpHeaders headers = HttpHeaders.of(headersBufferedReader);

        // when // then
        assertThatCode(() -> HttpBody.of(bodyBufferedReader, headers))
                .doesNotThrowAnyException();
    }
}