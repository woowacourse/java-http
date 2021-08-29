package nextstep.jwp.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UriPath 테스트")
class UriPathTest {

    @DisplayName("UriPath 파싱 테스트")
    @Test
    void parseUriPath() {
        //given
        final String uriPathValue = "/login?account=inbi&password=1234";

        //when
        final UriPath uriPath = new UriPath(uriPathValue);

        //then
        assertThat(uriPath.getUri()).isEqualTo("/login");
    }
}