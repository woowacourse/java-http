package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @DisplayName("header parsing test")
    @Test
    void headerParse() throws IOException {
        //given
        String httpMessage = "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Accept: */*";

        InputStream inputStream = new ByteArrayInputStream(httpMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        //when
        RequestHeaders requestHeaders = new RequestHeaders(bufferedReader);

        //then
        assertThat(requestHeaders.getHeader("Host")).contains("localhost:8080");
        assertThat(requestHeaders.getHeader("Connection")).contains("keep-alive");
        assertThat(requestHeaders.getHeader("Accept")).contains("*/*");
    }

    @DisplayName("header contain body parsing test")
    @Test
    void headerParseWithBody() throws IOException {
        //given
        String httpMessage = "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Accept: */*" + ""
                + "requestBodyExample: example";

        InputStream inputStream = new ByteArrayInputStream(httpMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        //when
        RequestHeaders requestHeaders = new RequestHeaders(bufferedReader);

        //then
        assertThatThrownBy(() -> requestHeaders.getHeader("requestBodyExample"))
                .isInstanceOf(RuntimeException.class);
    }
}