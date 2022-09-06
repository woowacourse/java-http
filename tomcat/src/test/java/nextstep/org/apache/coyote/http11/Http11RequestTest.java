package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.http11.Http11Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import support.RequestFixture;
import support.StubSocket;

class Http11RequestTest {

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
        final Http11Request request = Http11Request.of(stubSocket.getInputStream());

        // then
        assertThat(request.hasPath("/")).isTrue();
    }

    @Test
    void isForStaticFile() {
    }
}
