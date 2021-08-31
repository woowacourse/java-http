package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestStartLineTest {

    @DisplayName("startLine Parsing Test")
    @Test
    void StartLineParse() throws IOException {
        //given
        String httpMessage = "GET /index.html HTTP/1.1\n";

        InputStream inputStream = new ByteArrayInputStream(httpMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        //when

        RequestStartLine requestStartLine = RequestStartLine.create(bufferedReader);
        //then

        assertThat(requestStartLine.getMethod()).isEqualTo(Method.GET);
        assertThat(requestStartLine.getPath()).isEqualTo("/index.html");
        assertThat(requestStartLine.getVersionOfProtocol()).isEqualTo("HTTP/1.1");
    }

    @DisplayName("startLine contain queryString Parsing Test")
    @Test
    void StartLineWithQueryStringParse() throws IOException {
        //given
        String httpMessage = "GET /login?account=gugu&password=password HTTP/1.1\n";

        InputStream inputStream = new ByteArrayInputStream(httpMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        //when

        RequestStartLine requestStartLine = RequestStartLine.create(bufferedReader);
        //then

        assertThat(requestStartLine.getMethod()).isEqualTo(Method.GET);
        assertThat(requestStartLine.getPath()).isEqualTo("/login");
        assertThat(requestStartLine.getVersionOfProtocol()).isEqualTo("HTTP/1.1");
        assertThat(requestStartLine.getAttribute("account")).isEqualTo("gugu");
        assertThat(requestStartLine.getAttribute("password")).isEqualTo("password");
    }
}