package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.request.request_line.HttpMethod;
import nextstep.jwp.http.request.request_line.HttpPath;
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

    @DisplayName("존재하지 않는 httpHeader는 가져오지 못한다.")
    @Test
    void getHeader_nonExists_fail() {
        assertThatCode(() -> httpRequest.getHeader("Host2")
            .orElseThrow(IllegalArgumentException::new))
            .isInstanceOf(IllegalArgumentException.class);
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