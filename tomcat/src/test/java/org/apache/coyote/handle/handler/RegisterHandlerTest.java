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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RegisterHandlerTest {

    @Test
    void GET_요청시_등록_페이지를_반환한다() throws Exception {
        final String httpRequestMessage = String.join("\r\n",
                "GET /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive"
        );
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);

        final RegisterHandler registerHandler = new RegisterHandler();
        registerHandler.doGet(httpRequest, httpResponse);

        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 4319 ",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
        assertThat(httpResponse).hasToString(expected);
    }

    @Nested
    class POST_요청_처리 {

        @Test
        void 회원_등록에_성공하면_홈_페이지_요청으로_리다이렉트한다() throws Exception {
            final String body = "account=gugu1&email=gugu@naver.com&password=password";
            final String httpRequestMessage = String.join("\r\n",
                    "POST /register HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Content-Length: " + body.getBytes().length,
                    "",
                    body
            );
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);

            final RegisterHandler registerHandler = new RegisterHandler();
            registerHandler.doPost(httpRequest, httpResponse);

            assertThat(httpResponse.toString())
                    .contains("HTTP/1.1 302 Found ", "Location: /index.html ", "Set-Cookie: JSESSIONID=");
        }

        @Test
        void 계정_정보가_존재하지_않으면_등록_실패_페이지_요청으로_리다이렉트한다() throws Exception {
            final String body = "email=gugu@naver.com&password=password";
            final String httpRequestMessage = String.join("\r\n",
                    "POST /register HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Content-Length: " + body.getBytes().length,
                    "",
                    body
            );
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);

            final RegisterHandler registerHandler = new RegisterHandler();
            registerHandler.doPost(httpRequest, httpResponse);

            final String expected = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: /400.html ",
                    "",
                    null
            );
            assertThat(httpResponse).hasToString(expected);
        }

        @Test
        void 비밀번호_정보가_존재하지_않으면_등록_실패_페이지_요청으로_리다이렉트한다() throws Exception {
            final String body = "account=gugu1&email=gugu@naver.com";
            final String httpRequestMessage = String.join("\r\n",
                    "POST /register HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Content-Length: " + body.getBytes().length,
                    "",
                    body
            );
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);

            final RegisterHandler registerHandler = new RegisterHandler();
            registerHandler.doPost(httpRequest, httpResponse);

            final String expected = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: /400.html ",
                    "",
                    null
            );
            assertThat(httpResponse).hasToString(expected);
        }

        @Test
        void 이메일_정보가_존재하지_않으면_등록_실패_페이지_요청으로_리다이렉트한다() throws Exception {
            final String body = "account=gugu1&password=password";
            final String httpRequestMessage = String.join("\r\n",
                    "POST /register HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Content-Length: " + body.getBytes().length,
                    "",
                    body
            );
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);

            final RegisterHandler registerHandler = new RegisterHandler();
            registerHandler.doPost(httpRequest, httpResponse);

            final String expected = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: /400.html ",
                    "",
                    null
            );
            assertThat(httpResponse).hasToString(expected);
        }

        @Test
        void 이미_존재하는_계정이라면_등록_실패_페이지_요청으로_리다이렉트한다() throws Exception {
            final String body = "account=gugu&email=gugu@naver.com&password=password";
            final String httpRequestMessage = String.join("\r\n",
                    "POST /register HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Content-Length: " + body.getBytes().length,
                    "",
                    body
            );
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);

            final RegisterHandler registerHandler = new RegisterHandler();
            registerHandler.doPost(httpRequest, httpResponse);

            final String expected = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: /400.html ",
                    "",
                    null
            );
            assertThat(httpResponse).hasToString(expected);
        }
    }
}
