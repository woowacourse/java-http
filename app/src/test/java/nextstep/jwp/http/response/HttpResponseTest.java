package nextstep.jwp.http.response;

import nextstep.jwp.http.exception.UnsupportedExtensionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class HttpResponseTest {

    private OutputStream outputStream;

    @BeforeEach
    private void setUp() throws FileNotFoundException {
        outputStream = new FileOutputStream( "src/test/resources/test.txt");
    }

    @DisplayName("200 OK 응답 테스트")
    @Test
    void ok() throws IOException {
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 5564\r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));


        HttpResponse httpResponse = new HttpResponse(outputStream, "HTTP/1.1");
        httpResponse.status(HttpResponseStatus.OK);
        httpResponse.resource("/index.html");
        httpResponse.write();

        String result = new String(Files.readAllBytes(Path.of("src/test/resources/test.txt")));

        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("404 Not Found 응답 테스트")
    @Test
    void notfound() throws IOException {
        String expected = "HTTP/1.1 404 Not Found\r\n" +
                "\r\n";

        HttpResponse httpResponse = new HttpResponse(outputStream, "HTTP/1.1");
        httpResponse.status(HttpResponseStatus.NOT_FOUND);
        httpResponse.write();

        String result = new String(Files.readAllBytes(Path.of("src/test/resources/test.txt")));

        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("302 Redirect 응답 테스트")
    @Test
    void redirect() throws IOException {
        String expected = "HTTP/1.1 302 Found\r\n" +
                "Location: /index.html\r\n" +
                "\r\n";

        HttpResponse httpResponse = new HttpResponse(outputStream, "HTTP/1.1");
        httpResponse.status(HttpResponseStatus.FOUND);
        httpResponse.location("/index.html");
        httpResponse.write();

        String result = new String(Files.readAllBytes(Path.of("src/test/resources/test.txt")));

        assertThat(result).isEqualTo(expected);
    }

    @AfterEach
    private void tearDown() throws IOException {
        Files.delete(Path.of("src/test/resources/test.txt"));
    }

}