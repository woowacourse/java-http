package org.apache.coyote.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseBuilderTest {

    private HttpResponse response;


    @BeforeEach
    void setUp() {
        response = new HttpResponse();
    }

    @DisplayName("정적 파일의 내용으로 응답 객체를 build한다.")
    @Test
    void buildStaticContent() throws IOException {
        // given
        String fileName = "static/test-target.txt";
        URL resource = getClass().getClassLoader().getResource(fileName);
        List<String> fileContent = List.of(new String(Files.readAllBytes(new File(resource.getFile()).toPath())).split(
                "\r\n"));

        // when
        HttpResponseBuilder.buildStaticContent(response, fileName , fileContent);

        // then
        String expected = String.join(
                "\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 24 ",
                "",
                String.join("\r\n", fileContent)
        );
        byte[] responseBytes = response.getBytes();
        assertThat(responseBytes).isEqualTo(expected.getBytes());
    }

    @DisplayName("302 응답을 build한다.")
    @Test
    void setRedirection() {
        // given & when
        HttpResponseBuilder.buildRedirection(response, "/");

        // then
        String expected = String.join(
                "\r\n",
                "HTTP/1.1 302 Found ",
                "Location: / ",
                "Content-Length: 0 ",
                "",
                ""
        );
        byte[] responseBytes = response.getBytes();
        assertThat(responseBytes).isEqualTo(expected.getBytes());
    }

    @DisplayName("404 응답을 build한다.")
    @Test
    void buildNotFound() {
        // given & when
        HttpResponseBuilder.buildNotFound(response, List.of("Not Found"));

        // then
        String expected = String.join(
                "\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 9 ",
                "",
                "Not Found"
        );
        byte[] responseBytes = response.getBytes();
        assertThat(responseBytes).isEqualTo(expected.getBytes());
    }
}
