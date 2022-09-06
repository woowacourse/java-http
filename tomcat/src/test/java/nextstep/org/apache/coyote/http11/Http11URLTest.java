package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.Http11URL;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11URLTest {
    private StubSocket stubSocket;

    @Test
    void isDefault() {
        // given
        final Http11URL http11URL = new Http11URL("/css/styles.css");

        // when
        final boolean actual = http11URL.isDefault();

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void of() {
        // given
        final Http11URL http11URL = Http11URL.of("/login?account=account&password=password");

        // when
        final boolean actual = http11URL.hasPath("/login");

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void getMediaType() throws IOException {
        // given
        final Http11URL http11URL = new Http11URL("/css/styles.css");

        // when
        final String actual = http11URL.getMIMEType();

        // then
        assertThat(actual).isEqualTo("text/css");
    }


    @Test
    void getMediaType_default() throws IOException {
        // given
        final Http11URL http11URL = new Http11URL("/");

        // when
        final String actual = http11URL.getMIMEType();

        // then
        assertThat(actual).isEqualTo("text/html");
    }

    @Test
    void getMediaType_notStaticFile() throws IOException {
        // given
        final Http11URL http11URL = new Http11URL("/login");

        // when
        final String actual = http11URL.getMIMEType();

        // then
        assertThat(actual).isEqualTo("text/html");
    }

    @Test
    void read() throws IOException, URISyntaxException {
        // given
        final Http11URL http11URL = new Http11URL("/staticFile.txt");

        // when
        final String actual = http11URL.read();

        // then
        assertThat(actual).contains("This is static file.");
    }
}
