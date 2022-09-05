package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11StaticFile;
import org.apache.coyote.http11.Http11URL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import support.RequestFixture;
import support.ResponseFixture;
import support.StubSocket;

class Http11ResponseTest {

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
        final Http11Response http11Response = new Http11Response(stubSocket.getOutputStream());

        // when
        final Http11URL urlPath = Http11URL.of(stubSocket.getInputStream());
        http11Response.write(Http11StaticFile.of(urlPath));

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        assert resource != null;
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = ResponseFixture.create(HttpStatus.OK, "text/html", body);

        assertThat(expected).isEqualTo(stubSocket.output());
    }
}
