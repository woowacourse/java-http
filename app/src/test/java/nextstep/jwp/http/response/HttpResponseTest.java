package nextstep.jwp.http.response;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;


class HttpResponseTest {

    private OutputStream outputStream;

    @BeforeEach
    private void setUp() throws FileNotFoundException {
        outputStream = new FileOutputStream( "src/test/resources/test.txt");
    }

    @Test
    void ok() throws IOException {
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        HttpResponse httpResponse = new HttpResponse(outputStream);
        httpResponse.ok("/index.html");

        String result = new String(Files.readAllBytes(Path.of("src/test/resources/test.txt")));

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void notfound() throws IOException {
        String expected = "HTTP/1.1 404 Not Found \r\n" +
                "\r\n";

        HttpResponse httpResponse = new HttpResponse(outputStream);
        httpResponse.notFound();

        String result = new String(Files.readAllBytes(Path.of("src/test/resources/test.txt")));

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void redirect() throws IOException {
        String expected = "HTTP/1.1 302 Redirect \r\n" +
                "Location: /index.html \r\n" +
                "\r\n";

        HttpResponse httpResponse = new HttpResponse(outputStream);
        httpResponse.redirect("/index.html");

        String result = new String(Files.readAllBytes(Path.of("src/test/resources/test.txt")));

        assertThat(result).isEqualTo(expected);
    }

    @AfterEach
    private void tearDown() throws IOException {
        Files.delete(Path.of("src/test/resources/test.txt"));
    }

}