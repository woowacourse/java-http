package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("RequestLine을 입력받으면, 파싱해서 보관한다.")
    @Test
    void requestLineTest() throws IOException {
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");

        assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getHttpVersion()).isEqualTo("HTTP/1.1");
        assertThat(requestLine.getUrl()).isEqualTo("/index.html");
    }

    @DisplayName("QueryString이 있는 url을 받으면, 파싱해서 보관한다.")
    @Test
    void QueryStringTest() throws IOException {
        RequestLine requestLine = new RequestLine("GET /login?account=aaa&password=bbb HTTP/1.1");
        assertThat(requestLine.getQueryParam("account")).isEqualTo("aaa");
        assertThat(requestLine.getQueryParam("password")).isEqualTo("bbb");
    }
}