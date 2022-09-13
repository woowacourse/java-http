package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        final InputStream inputStream = new ByteArrayInputStream(requestLine.getBytes());
        final Request request = Request.of(new BufferedReader(new InputStreamReader(inputStream)));

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
