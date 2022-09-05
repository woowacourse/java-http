package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.exception.StaticFileNotFoundException;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.http11.Http11StaticFile;
import org.apache.coyote.http11.Http11URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import support.RequestFixture;
import support.StubSocket;

class Http11StaticFileTest {

    private StubSocket stubSocket;

    @AfterEach
    void afterEach() throws IOException {
        stubSocket.close();
    }

    @Test
    void of() throws IOException, URISyntaxException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        stubSocket = new StubSocket(httpRequest);

        // when
        final Http11URI urlPath = Http11URI.of(stubSocket.getInputStream());
        final Http11StaticFile staticFile = Http11StaticFile.of(urlPath);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        assert resource != null;
        final String content = new String(Files.readAllBytes(Path.of(resource.toURI())));
        assertThat(staticFile.getContent()).isEqualTo(content);
    }

    @Test
    void getContentType() throws IOException, URISyntaxException {
        // given
        final String httpCssRequest = RequestFixture.create(HttpMethod.GET, "/css/styles.css", "");
        stubSocket = new StubSocket(httpCssRequest);
        final Http11URI urlPath = Http11URI.of(stubSocket.getInputStream());
        final Http11StaticFile cssFile = Http11StaticFile.of(urlPath);

        // when
        final String actual = cssFile.getContentType();

        // then
        assertThat(actual).isEqualTo("text/css");
    }

    @Test
    void throwExceptionNotExistsFile() throws IOException {
        // given
        final String httpCssRequest = RequestFixture.create(HttpMethod.GET, "/notExist.html", "");
        stubSocket = new StubSocket(httpCssRequest);
        final Http11URI urlPath = Http11URI.of(stubSocket.getInputStream());

        // when, then
        assertThatThrownBy(() -> Http11StaticFile.of(urlPath))
                .isExactlyInstanceOf(StaticFileNotFoundException.class);
    }
}
