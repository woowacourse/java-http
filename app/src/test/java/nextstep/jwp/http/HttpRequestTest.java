package nextstep.jwp.http;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void checkMethod() throws IOException {
        String fileName = "get.txt";
        InputStream in = new FileInputStream(getClass().getClassLoader().getResource(fileName).getFile());
        HttpRequest request = HttpRequest.of(in);

        assertThat(request.checkMethod("GET")).isTrue();
    }
}