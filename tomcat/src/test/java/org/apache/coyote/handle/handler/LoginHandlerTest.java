package org.apache.coyote.handle.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.Session;
import org.apache.coyote.common.SessionManager;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LoginHandlerTest {

    @Nested
    class GET_요청_처리 {

        @Test
        void 세션_쿠키가_있으면_홈_페이지_요청으로_리다이렉트_한다() throws Exception {
            final String httpRequestMessage = String.join("\r\n",
                    "GET /login HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Cookie: JSESSIONID=123"
            );
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);
            final Session session = new Session("123");
            session.setAttribute("user", "user");
            SessionManager.add(session);

            final LoginHandler loginHandler = new LoginHandler();
            loginHandler.doGet(httpRequest, httpResponse);

            final String expected = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: /index.html ",
                    "",
                    null
            );
            assertThat(httpResponse).hasToString(expected);
        }

        @Test
        void 세션_쿠키가_없으면_로그인_페이지를_반환한다() throws Exception {
            final String httpRequestMessage = String.join("\r\n",
                    "GET /login HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive"
            );
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);

            final LoginHandler loginHandler = new LoginHandler();
            loginHandler.doGet(httpRequest, httpResponse);

            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            final String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 3797 ",
                    "",
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
            );
            assertThat(httpResponse).hasToString(expected);
        }

        @Test
        void 세션_정보가_없으면_로그인_페이지를_반환한다() throws Exception {
            final String httpRequestMessage = String.join("\r\n",
                    "GET /login HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Cookie: JSESSIONID=123"
            );
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);

            final LoginHandler loginHandler = new LoginHandler();
            loginHandler.doGet(httpRequest, httpResponse);

            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            final String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 3797 ",
                    "",
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
            );
            assertThat(httpResponse).hasToString(expected);
        }
    }

    @Nested
    class POST_요청_처리 {

        @Test
        void 로그인에_성공하면_홈_페이지_요청으로_리다이렉트_한다() throws Exception {
            final String body = "account=gugu&password=password";
            final String httpRequestMessage = String.join("\r\n",
                    "POST /login HTTP/1.1",
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

            final LoginHandler loginHandler = new LoginHandler();
            loginHandler.doPost(httpRequest, httpResponse);

            assertThat(httpResponse.toString())
                    .contains("HTTP/1.1 302 Found ", "Location: /index.html ", "Set-Cookie: JSESSIONID=");
        }

        @Test
        void 계정_정보가_없으면_로그인_실패_페이지_요청으로_리다이렉트_한다() throws Exception {
            final String body = "password=password";
            final String httpRequestMessage = String.join("\r\n",
                    "POST /login HTTP/1.1",
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

            final LoginHandler loginHandler = new LoginHandler();
            loginHandler.doPost(httpRequest, httpResponse);

            final String expected = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: /401.html ",
                    "",
                    null
            );
            assertThat(httpResponse).hasToString(expected);
        }

        @Test
        void 비밀번호_정보가_없으면_로그인_실패_페이지_요청으로_리다이렉트_한다() throws Exception {
            final String body = "account=gugu";
            final String httpRequestMessage = String.join("\r\n",
                    "POST /login HTTP/1.1",
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

            final LoginHandler loginHandler = new LoginHandler();
            loginHandler.doPost(httpRequest, httpResponse);

            final String expected = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: /401.html ",
                    "",
                    null
            );
            assertThat(httpResponse).hasToString(expected);
        }

        @Test
        void 존재하지_않는_계정이면_로그인_실패_페이지_요청으로_리다이렉트_한다() throws Exception {
            final String body = "account=gugu100&password=password";
            final String httpRequestMessage = String.join("\r\n",
                    "POST /login HTTP/1.1",
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

            final LoginHandler loginHandler = new LoginHandler();
            loginHandler.doPost(httpRequest, httpResponse);

            final String expected = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: /401.html ",
                    "",
                    null
            );
            assertThat(httpResponse).hasToString(expected);
        }

        @Test
        void 비밀번호가_일치하지_않으면_로그인_실패_페이지_요청으로_리다이렉트_한다() throws Exception {
            final String body = "account=gugu&password=password1";
            final String httpRequestMessage = String.join("\r\n",
                    "POST /login HTTP/1.1",
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

            final LoginHandler loginHandler = new LoginHandler();
            loginHandler.doPost(httpRequest, httpResponse);

            final String expected = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: /401.html ",
                    "",
                    null
            );
            assertThat(httpResponse).hasToString(expected);
        }
    }
}
