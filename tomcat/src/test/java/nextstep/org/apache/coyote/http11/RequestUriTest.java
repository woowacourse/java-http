package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.MediaType;
import org.apache.coyote.http11.request.RequestUri;
import org.junit.jupiter.api.Test;

class RequestUriTest {

    @Test
    void requestUri에서_mediaType을_찾는다() {
        // given
        final String uri = "/css/styles.css";

        // when
        final RequestUri requestUri = RequestUri.of(uri);

        // then
        assertThat(requestUri.findMediaType()).isEqualTo(MediaType.CSS);
    }

    @Test
    void requestUri에서_mediaType이_없는_경우_기본_타입을_반환한다() {
        // given
        final String uri = "/login?account=gugu&password=password";

        // when
        final RequestUri requestUri = RequestUri.of(uri);

        // then
        assertThat(requestUri.findMediaType()).isEqualTo(MediaType.HTML);
    }

    @Test
    void uri에서_리소스_파일_path를_찾는다() {
        // given
        final String uri = "/login?account=gugu&password=password";

        // when
        final RequestUri requestUri = RequestUri.of(uri);

        // then
        assertThat(requestUri.getResourcePath()).isEqualTo("/login");
    }
}
