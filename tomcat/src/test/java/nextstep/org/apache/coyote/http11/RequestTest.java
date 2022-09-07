package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import support.RequestFixture;
import support.StubSocket;

class RequestTest {

    private StubSocket stubSocket;

    @AfterEach
    void tearDown() throws IOException {
        stubSocket.close();
    }

    @Test
    void of() throws IOException {
        // given
        final String requestString = RequestFixture.create(HttpMethod.GET, "/", "");
        stubSocket = new StubSocket(requestString);

        // when
        final Request request = Request.of(stubSocket.getInputStream());

        // then
        assertThat(request.hasPath("/")).isTrue();
    }

    @Test
    void isForStaticFile() throws IOException {
        // given
        final String requestString = RequestFixture.create(HttpMethod.GET, "/index.html", "");
        stubSocket = new StubSocket(requestString);
        final Request request = Request.of(stubSocket.getInputStream());

        // when
        final boolean actual = request.isForStaticFile();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void isDefaultUrl() throws IOException {
        // given
        final String requestString = RequestFixture.create(HttpMethod.GET, "/", "");
        stubSocket = new StubSocket(requestString);
        final Request request = Request.of(stubSocket.getInputStream());

        // when
        final boolean actual = request.isDefaultUrl();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void findJsessionid() throws IOException {
        // given
        final String requestString = RequestFixture.create(HttpMethod.GET, "/", "");
        stubSocket = new StubSocket(requestString);
        final Request request = Request.of(stubSocket.getInputStream());
        final String jsessionidValue = "JSESSIONID=some-jsessionid-exists";
        request.getHeaders().add("Cookie", jsessionidValue);

        // when
        final String actual = request.findJsessionid();

        // then
        assertThat(actual).isEqualTo(jsessionidValue);
    }
}
