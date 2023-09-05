package org.apache.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.request.HttpRequest;
import org.apache.response.HttpResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FileHandlerTest {

    @Nested
    class HTML_파일을_처리할_때 {

        @Test
        void 정상적으로_응답_후_200_상태코드를_반환한다() throws IOException {
            String httpRequestMessage = String.join("\r\n",
                    "GET /index.html HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive"
            );
            ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = HttpRequest.of(bufferedReader);
            FileHandler fileHandler = new FileHandler();

            HttpResponse httpResponse = fileHandler.handle(httpRequest);

            String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 "
            );
            assertThat(httpResponse.getResponse()).contains(expected);
        }

        @ParameterizedTest
        @ValueSource(strings = {"DELETE", "PUT", "PATCH"})
        void GET_또는_POST_요청이_아니면_405_상태코드를_발생한다(String method) throws IOException {
            String httpRequestMessage = String.join("\r\n",
                    ""+ method + " /index.html HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive"
            );
            ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = HttpRequest.of(bufferedReader);
            FileHandler fileHandler = new FileHandler();

            HttpResponse httpResponse = fileHandler.handle(httpRequest);

            String expected = String.join("\r\n",
                    "HTTP/1.1 405 METHOD_NOT_ALLOWED ",
                    "Content-Type: text/html;charset=utf-8 "
            );
            assertThat(httpResponse.getResponse()).contains(expected);
        }
    }
}
