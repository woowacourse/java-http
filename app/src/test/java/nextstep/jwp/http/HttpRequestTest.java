package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import nextstep.TestUtil;
import nextstep.jwp.http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("Http 요청 메세지를 읽어 HttpRequest 객체를 생성한다.")
    @Test
    void create() throws IOException {
        String firstLine = "GET /login?account=gugu&password=password HTTP/1.1";
        HttpRequest httpRequest = TestUtil.createRequest(firstLine);

        assertThat(httpRequest.getHttpMethod().name()).isEqualTo("GET");
        assertThat(httpRequest.getPath()).isEqualTo("/login");
        assertThat(httpRequest.getQueryStrings().getAllQueryStrings()).hasSize(2);
        assertThat(httpRequest.getProtocol().getValue()).isEqualTo("HTTP/1.1");
        assertThat(httpRequest.getHttpHeader().getAllHeaders()).hasSize(3);
    }
}