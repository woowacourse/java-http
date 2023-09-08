package org.apache.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;
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
class FileControllerTest {

    @Nested
    class HTML_파일을_처리할_때 {

        @Test
        void 정상적으로_응답_후_200_상태코드를_반환한다() throws Exception {
            String httpRequestMessage = String.join("\r\n",
                    "GET /index.html HTTP/1.1",
                    "Host: localhost:8080"
            );
            ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            HttpResponse httpResponse = new HttpResponse();

            AbstractController controller = RequestMapping.findController(httpRequest);
            controller.service(httpRequest, httpResponse);

            List<String> responses = List.of(
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 "
            );
            assertThat(httpResponse.getResponse()).contains(responses.get(0), responses.get(1));
        }

        @ParameterizedTest
        @ValueSource(strings = {"DELETE", "PUT", "PATCH"})
        void GET_또는_POST_요청이_아니면_405_상태코드를_발생한다(String method) throws Exception {
            String httpRequestMessage = String.join("\r\n",
                    "" + method + " /index.html HTTP/1.1",
                    "Host: localhost:8080"
            );
            ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            HttpResponse httpResponse = new HttpResponse();

            AbstractController controller = RequestMapping.findController(httpRequest);
            controller.service(httpRequest, httpResponse);

            List<String> responses = List.of(
                    "HTTP/1.1 405 METHOD_NOT_ALLOWED ",
                    "Content-Type: text/html;charset=utf-8 "
            );
            assertThat(httpResponse.getResponse()).contains(responses.get(0), responses.get(1));
        }
    }
}
