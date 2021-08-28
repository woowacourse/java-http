package nextstep.jwp.model;

import nextstep.jwp.model.http.HttpRequest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static nextstep.jwp.model.http.HTTPMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void request_GET_INDEXT() throws IOException {
        HttpRequest httpRequest = readRequest("./src/test/resources/index.txt");

        assertThat(httpRequest.getMethod()).isEqualTo(GET);
        assertThat(httpRequest.getPath()).isEqualTo("/index.html");
    }

    @Test
    void request_GET_LOGIN() throws IOException {
        HttpRequest request = readRequest("./src/test/resources/login.txt");

        assertThat(request.getMethod()).isEqualTo(GET);
        assertThat(request.getPath()).isEqualTo("/login");
    }

    @Test
    void request_CSS() throws IOException {
        HttpRequest request = readRequest("./src/test/resources/css.txt");

        assertThat(request.getMethod()).isEqualTo(GET);
        assertThat(request.getPath()).isEqualTo("/css/styles.css");
    }

    private HttpRequest readRequest(String url) throws IOException {
        File file = new File(url);
        FileInputStream inputStream = new FileInputStream(file);
        return new HttpRequest(inputStream);
    }
}