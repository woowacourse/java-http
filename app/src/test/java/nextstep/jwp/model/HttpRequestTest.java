package nextstep.jwp.model;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void request_GET_INDEXT() throws IOException {
        File file = new File("./src/test/resources/index.txt");
        FileInputStream inputStream = new FileInputStream(file);
        HttpRequest httpRequest = new HttpRequest(inputStream);

        assertThat(httpRequest.getHttpMethod()).isEqualTo("GET");
        assertThat(httpRequest.getURL()).isEqualTo("/index.html");
    }

    @Test
    void request_GET_LOGIN() throws IOException {
        File file = new File("./src/test/resources/login.txt");
        FileInputStream inputStream = new FileInputStream(file);
        HttpRequest httpRequest = new HttpRequest(inputStream);

        assertThat(httpRequest.getHttpMethod()).isEqualTo("GET");
        assertThat(httpRequest.getURL()).isEqualTo("/login");
    }

    @Test
    void request_CSS() throws IOException {
        File file = new File("./src/test/resources/css.txt");
        FileInputStream inputStream = new FileInputStream(file);
        HttpRequest httpRequest = new HttpRequest(inputStream);

        assertThat(httpRequest.getHttpMethod()).isEqualTo("GET");
        assertThat(httpRequest.getURL()).isEqualTo("/css/styles.css");
    }
}