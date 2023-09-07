package org.apache.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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
class HomeControllerTest {

    @Nested
    class 기본_페이지_요청_시 {

        @Test
        void GET_요청이면_200_상태코드를_반환한다() throws Exception {
            String httpRequestMessage = String.join("\r\n",
                    "GET / HTTP/1.1",
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

        @ParameterizedTest
        @ValueSource(strings = {"POST", "DELETE", "PUT", "PATCH"})
        void GET_이외의_요청이면_405_상태코드를_반환한다(String method) throws Exception {
            String httpRequestMessage = String.join("\r\n",
                    ""+ method + " / HTTP/1.1",
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
                    "HTTP/1.1 405 METHOD_NOT_ALLOWED "
            );
            assertThat(httpResponse.getResponse()).contains(expected);
        }
    }
}
