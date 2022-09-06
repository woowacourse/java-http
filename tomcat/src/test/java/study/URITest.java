package study;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Test;

public class URITest {

    @Test
    void getFile() throws MalformedURLException {
        URL url = new URL("http://localhost:8080/index.html");

        assertThat(url.getFile()).isEqualTo("/index.html");
    }

    @Test
    void getPath() throws MalformedURLException {
        URL url = new URL("http://localhost:8080/index.html");

        assertThat(url.getPath()).isEqualTo("/index.html");
    }

    @Test
    void getPath2() throws MalformedURLException {
        URL url = new URL("http://localhost:8080/login?account=gugu&password=password");

        assertThat(url.getPath()).isEqualTo("/login");
    }

    @Test
    void getQuery() throws MalformedURLException {
        URL url = new URL("http://localhost:8080/login?account=gugu&password=password");

        assertThat(url.getQuery()).isEqualTo("account=gugu&password=password");
    }

    @Test
    void getQuery2() throws MalformedURLException {
        URL url = new URL("http://localhost:8080/login");

        assertThat(url.getQuery()).isBlank();
    }
}
