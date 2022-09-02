package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("쿼리 스트링에서 해당 파라미터에 맞는 값을 가져온다.")
    void getQueryString() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */*",
                "");
        final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final HttpRequest httpRequest = new HttpRequest(bufferedReader);

        // when
        final String account = httpRequest.getQueryString("account");
        final String password = httpRequest.getQueryString("password");

        // then
        assertAll(
                () -> assertThat(account).isEqualTo("gugu"),
                () -> assertThat(password).isEqualTo("password")
        );
    }
}
