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
class ResisterControllerTest {

    @Nested
    class 회원_등록_페이지_요청_시 {

        @Test
        void GET_요청이면_200_상태코드를_반환한다() throws Exception {
            String httpRequestMessage = String.join("\r\n",
                    "GET /register HTTP/1.1",
                    "Host: localhost:8080"
            );
            ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            HttpResponse httpResponse = new HttpResponse();

            RequestMapping requestMapping = new RequestMapping();
            AbstractController controller = requestMapping.findController(httpRequest);
            controller.service(httpRequest, httpResponse);

            String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK "
            );
            assertThat(httpResponse.getResponse()).contains(expected);
        }

        @Test
        void POST_요청이면_302_상태코드를_반환한다() throws Exception {
            String body = "account=gray&password=1234&email=gray@gmail.com";
            String httpRequestMessage = String.join("\r\n",
                    "POST /register HTTP/1.1",
                    "Host: localhost:8080",
                    "Content-Length: " + body.getBytes().length,
                    "",
                    body
            );
            ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            HttpResponse httpResponse = new HttpResponse();

            RequestMapping requestMapping = new RequestMapping();
            AbstractController controller = requestMapping.findController(httpRequest);
            controller.service(httpRequest, httpResponse);

            String expected = String.join("\r\n",
                    "HTTP/1.1 302 FOUND "
            );
            assertThat(httpResponse.getResponse()).contains(expected);
        }

        @ParameterizedTest
        @ValueSource(strings = {"DELETE", "PUT", "PATCH"})
        void GET_POST_요청이_아니면_405_상태코드를_반환한다(String method) throws Exception {
            String httpRequestMessage = String.join("\r\n",
                    ""+ method + " /register HTTP/1.1",
                    "Host: localhost:8080"
            );
            ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            HttpResponse httpResponse = new HttpResponse();

            RequestMapping requestMapping = new RequestMapping();
            AbstractController controller = requestMapping.findController(httpRequest);
            controller.service(httpRequest, httpResponse);

            List<String> responses = List.of(
                    "HTTP/1.1 405 METHOD_NOT_ALLOWED ",
                    "Content-Type: text/html;charset=utf-8 "
            );
            assertThat(httpResponse.getResponse()).contains(responses.get(0), responses.get(1));
        }
    }
}
