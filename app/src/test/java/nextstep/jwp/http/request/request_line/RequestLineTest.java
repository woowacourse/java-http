package nextstep.jwp.http.request.request_line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    private RequestLine requestLine;

    @BeforeEach
    void setUp() {
        requestLine = new RequestLine("GET /test.txt HTTP/1.1");
    }

    @Test
    void getHttpMethod() {
        String httpMethod = requestLine.getHttpMethod();

        assertThat(httpMethod).isEqualTo("GET");
    }

    @Test
    void getPath() {
        String path = requestLine.getPath();

        assertThat(path).isEqualTo("/test.txt");
    }

    @Test
    void getVersion() {
        String version = requestLine.getVersion();

        assertThat(version).isEqualTo("HTTP/1.1");
    }
}