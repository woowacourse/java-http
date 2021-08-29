package nextstep.jwp.webserver;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void parseRequest() {
        // given
        final String request = request("/index.html?aa=11");

        // when
        HttpRequest httpRequest = new HttpRequest(request);

        // then
        assertThat(httpRequest.getUri()).isEqualTo("/index.html");
        assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(httpRequest.getBody()).isEqualTo("");
        assertThat(httpRequest.getQueryParam("aa")).isEqualTo("11");
        assertThat(httpRequest.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(httpRequest.getHeader("Connection")).isEqualTo("keep-alive");
    }

    private String request(String path) {
        return String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
    }
}