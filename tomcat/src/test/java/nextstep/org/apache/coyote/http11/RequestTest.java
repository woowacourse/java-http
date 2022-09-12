package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;
import org.junit.jupiter.api.Test;
import support.RequestFixture;

class RequestTest {

    @Test
    void of() throws IOException {
        // given
        final String requestLine = RequestFixture.createLine(HttpMethod.GET, "/", "");

        // when
        final Request request = Request.of(new ByteArrayInputStream(requestLine.getBytes()));

        // then
        assertThat(request.hasPath("/")).isTrue();
    }

    @Test
    void isForStaticFile() throws IOException {
        // given
        final Request request = RequestFixture.create(HttpMethod.GET, "/index.html", "");

        // when
        final boolean actual = request.isForStaticFile();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void isDefaultUrl() throws IOException {
        // given
        final Request request = RequestFixture.create(HttpMethod.GET, "/", "");

        // when
        final boolean actual = request.isDefaultUrl();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void findJsessionid() throws IOException {
        // given
        final String jsessionidValue = "JSESSIONID=some-jsessionid-exists";
        final Request request = RequestFixture.create(HttpMethod.GET, "/", Map.of("Cookie", jsessionidValue), "");

        // when
        final boolean hasJsessionid = request.hasJsessionid();

        // then
        assertThat(hasJsessionid).isTrue();
    }
}
