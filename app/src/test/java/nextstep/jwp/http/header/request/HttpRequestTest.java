package nextstep.jwp.http.header.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.header.request.request_line.HttpMethod;
import nextstep.jwp.http.header.request.request_line.HttpPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    private HttpRequest httpRequest;

    @BeforeEach
    void setUp() {
        this.httpRequest = Fixture.getHttpRequest("/test");
    }

    @DisplayName("특정 httpHeader를 가져온다.")
    @Test
    void getHeader() {
        String host = httpRequest.getHeader("Host")
            .orElseThrow(IllegalArgumentException::new);

        String expected = Fixture.getFixtureHeaders().get("Host");

        assertThat(host).isEqualTo(expected);
    }

    @Test
    void getHttpMethod() {
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @Test
    void getPath() {
        HttpPath resourcePath = httpRequest.getPath();
        assertThat(resourcePath.getUri()).isEqualTo("/test");
    }
}
