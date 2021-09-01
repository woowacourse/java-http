package nextstep.jwp.http;

import nextstep.jwp.http.request.HttpRequest;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void checkMethod() throws IOException {
        String fileName = "get.txt";
        InputStream in = new FileInputStream("/" + getClass().getClassLoader().getResource(fileName).getFile());
        HttpRequest request = HttpRequest.of(in);

        assertThat(request.checkMethod("GET")).isTrue();
    }
}