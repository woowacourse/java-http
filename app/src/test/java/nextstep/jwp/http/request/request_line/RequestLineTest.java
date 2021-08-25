package nextstep.jwp.http.request.request_line;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.HttpVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    public static final String PATH = "test.txt";
    private RequestLine requestLine;

    @BeforeEach
    void setUp() {
        requestLine = new RequestLine(Fixture.createRequestLine(HttpMethod.GET, PATH));
    }

    @Test
    void getHttpMethod() {
        HttpMethod httpMethod = requestLine.getHttpMethod();

        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @Test
    void getPath() {
        HttpPath path = requestLine.getPath();

        assertThat(path.getUri()).isEqualTo(PATH);
    }

    @Test
    void getVersion() {
        HttpVersion version = requestLine.getVersion();

        assertThat(version).isEqualTo(HttpVersion.HTTP1_1);
    }
}