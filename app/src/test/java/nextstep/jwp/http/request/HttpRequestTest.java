package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import nextstep.jwp.fixture.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    private HttpRequest httpRequest;

    @BeforeEach
    void setUp() {
        String httpRequest = Fixture.getHttpRequest();
        this.httpRequest = new HttpRequest(httpRequest);
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
        String httpMethod = httpRequest.getHttpMethod();
        assertThat(httpMethod).isEqualTo("GET");
    }

    @Test
    void getResourcePath() {
        String resourcePath = httpRequest.getResourcePath();
        assertThat(resourcePath).isEqualTo(Fixture.getResourcePath());
    }
}