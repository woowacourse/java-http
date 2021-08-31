package nextstep.jwp.webserver;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void parseRequest() {
        // given
        String request = String.join("\r\n",
                "GET " + "/index.html?aa=11" + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when
        HttpRequest httpRequest = new HttpRequest(bufferedReader);

        // then
        assertThat(httpRequest.getUri()).isEqualTo("/index.html");
        assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(httpRequest.getQueryParam("aa")).isEqualTo("11");
        assertThat(httpRequest.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(httpRequest.getHeader("Connection")).isEqualTo("keep-alive");
    }
}