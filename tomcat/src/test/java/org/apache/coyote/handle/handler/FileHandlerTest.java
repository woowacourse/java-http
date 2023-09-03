package org.apache.coyote.handle.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FileHandlerTest {

    @Test
    void GET_요청시_정적_파일을_반환한다() throws Exception {
        final String httpRequestMessage = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive"
        );
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);

        final FileHandler fileHandler = new FileHandler();
        fileHandler.doGet(httpRequest, httpResponse);

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
        assertThat(httpResponse).hasToString(expected);
    }
}
