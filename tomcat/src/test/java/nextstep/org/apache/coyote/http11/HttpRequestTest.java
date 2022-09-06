package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.HttpReader;
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
        final HttpReader httpReader = new HttpReader(bufferedReader);
        final HttpRequest httpRequest = new HttpRequest(httpReader);

        // when
        final String account = httpRequest.getQueryStringValue("account");
        final String password = httpRequest.getQueryStringValue("password");

        // then
        assertAll(
                () -> assertThat(account).isEqualTo("gugu"),
                () -> assertThat(password).isEqualTo("password")
        );
    }

    @Test
    @DisplayName("해당 요청이 확장자를 가진 파일에 대한 요청인지 확인한다.")
    void isFileRequest() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive",
                "");
        final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final HttpReader httpReader = new HttpReader(bufferedReader);
        final HttpRequest httpRequest = new HttpRequest(httpReader);

        // when
        final boolean fileRequest = httpRequest.isFileRequest();

        // then
        assertThat(fileRequest).isTrue();
    }
}
