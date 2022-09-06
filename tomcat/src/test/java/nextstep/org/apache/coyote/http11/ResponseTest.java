package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import support.RequestFixture;
import support.ResponseFixture;
import support.StubSocket;

class ResponseTest {

    private StubSocket stubSocket;

    @AfterEach
    void afterEach() throws IOException {
        stubSocket.close();
    }

    @Test
    void write() throws IOException, URISyntaxException {
        // given
        final String httpRequest = RequestFixture.create(HttpMethod.GET, "/index.html", "");
        stubSocket = new StubSocket(httpRequest);
        final Response response = Response.of(stubSocket.getOutputStream());

        // when
        response.write(HttpStatus.OK, "/index.html");

        // then
        final java.net.URL resource = getClass().getClassLoader().getResource("static/index.html");
        assert resource != null;
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = ResponseFixture.create(HttpStatus.OK, "text/html", body);

        assertThat(expected).isEqualTo(stubSocket.output());
    }
}
