package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.URL;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class URLTest {
    private StubSocket stubSocket;

    @Test
    void isDefault() {
        // given
        final URL URL = new URL("/css/styles.css");

        // when
        final boolean actual = URL.isDefault();

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void of() {
        // given
        final URL url = URL.of("/login?account=account&password=password");

        // when
        final boolean actual = url.hasPath("/login");

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void getMediaType() throws IOException {
        // given
        final URL URL = new URL("/css/styles.css");

        // when
        final String actual = URL.getMIMEType();

        // then
        assertThat(actual).isEqualTo("text/css");
    }


    @Test
    void getMediaType_default() throws IOException {
        // given
        final URL URL = new URL("/");

        // when
        final String actual = URL.getMIMEType();

        // then
        assertThat(actual).isEqualTo("text/html");
    }

    @Test
    void getMediaType_notStaticFile() throws IOException {
        // given
        final URL URL = new URL("/login");

        // when
        final String actual = URL.getMIMEType();

        // then
        assertThat(actual).isEqualTo("text/html");
    }

    @Test
    void read() throws IOException, URISyntaxException {
        // given
        final URL URL = new URL("/staticFile.txt");

        // when
        final String actual = URL.read();

        // then
        assertThat(actual).contains("This is static file.");
    }
}
