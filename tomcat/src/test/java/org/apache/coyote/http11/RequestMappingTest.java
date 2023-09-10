package org.apache.coyote.http11;

import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestMappingTest {

    @Nested
    class getController_테스트 {

        @Test
        void HomeController_객체를_반환한다() throws IOException {
            // given
            final var requestLine = "GET / HTTP/1.1";
            final var requestHeader = String.join("\r\n",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Accept: */*",
                    "");
            final var request = String.join("\r\n", requestLine, requestHeader, "");
            final var inputStream = new ByteArrayInputStream(request.getBytes());
            final var inputStreamReader = new InputStreamReader(inputStream);
            final var bufferedReader = new BufferedReader(inputStreamReader);
            final var httpRequest = HttpRequest.from(bufferedReader);

            // when
            final var controller = RequestMapping.getController(httpRequest);

            // then
            assertThat(controller).isInstanceOf(HomeController.class);
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        }

        @Test
        void LoginController_객체를_반환한다() throws IOException {
            // given
            final var requestLine = "GET /login HTTP/1.1";
            final var requestHeader = String.join("\r\n",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Accept: */*",
                    "");
            final var request = String.join("\r\n", requestLine, requestHeader, "");
            final var inputStream = new ByteArrayInputStream(request.getBytes());
            final var inputStreamReader = new InputStreamReader(inputStream);
            final var bufferedReader = new BufferedReader(inputStreamReader);
            final var httpRequest = HttpRequest.from(bufferedReader);

            // when
            final var controller = RequestMapping.getController(httpRequest);

            // then
            assertThat(controller).isInstanceOf(LoginController.class);
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        }

        @Test
        void RegisterController_객체를_반환한다() throws IOException {
            // given
            final var requestLine = "GET /register HTTP/1.1";
            final var requestHeader = String.join("\r\n",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Accept: */*",
                    "");
            final var request = String.join("\r\n", requestLine, requestHeader, "");
            final var inputStream = new ByteArrayInputStream(request.getBytes());
            final var inputStreamReader = new InputStreamReader(inputStream);
            final var bufferedReader = new BufferedReader(inputStreamReader);
            final var httpRequest = HttpRequest.from(bufferedReader);

            // when
            final var controller = RequestMapping.getController(httpRequest);

            // then
            assertThat(controller).isInstanceOf(RegisterController.class);
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        }

        @Test
        void ResourceController_객체를_반환한다() throws IOException {
            // given
            final var requestLine = "GET /index.html HTTP/1.1";
            final var requestHeader = String.join("\r\n",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Accept: */*",
                    "");
            final var request = String.join("\r\n", requestLine, requestHeader, "");
            final var inputStream = new ByteArrayInputStream(request.getBytes());
            final var inputStreamReader = new InputStreamReader(inputStream);
            final var bufferedReader = new BufferedReader(inputStreamReader);
            final var httpRequest = HttpRequest.from(bufferedReader);

            // when
            final var controller = RequestMapping.getController(httpRequest);

            // then
            assertThat(controller).isInstanceOf(ResourceController.class);
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        }
    }
}
