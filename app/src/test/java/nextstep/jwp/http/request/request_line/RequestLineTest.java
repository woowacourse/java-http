package nextstep.jwp.http.request.request_line;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.HttpVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    private RequestLine requestLine;

    @BeforeEach
    void setUp() {
        requestLine = new RequestLine(Fixture.getHttpRequest());
    }

    @Test
    void getHttpMethod() {
        HttpMethod httpMethod = requestLine.getHttpMethod();

        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @Test
    void getPath() {
        HttpPath path = requestLine.getPath();

        assertThat(path.getPath()).isEqualTo(Fixture.getResourcePath());
    }

    @Test
    void getVersion() {
        HttpVersion version = requestLine.getVersion();

        assertThat(version).isEqualTo(HttpVersion.HTTP1_1);
    }
}