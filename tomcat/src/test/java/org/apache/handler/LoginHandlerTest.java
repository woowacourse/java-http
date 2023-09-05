package org.apache.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.common.Session;
import org.apache.common.SessionManager;
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
class LoginHandlerTest {

    @Nested
    class 로그인_요청_시 {

        @Test
        void GET_요청이면_200_상태코드를_반환한다() throws IOException {
            String httpRequestMessage = String.join("\r\n",
                    "GET /login HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive"
            );
            ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            LoginHandler loginHandler = new LoginHandler();

            HttpResponse httpResponse = loginHandler.handle(httpRequest);

            String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK "
            );
            assertThat(httpResponse.getResponse()).contains(expected);
        }

        @Test
        void GET_요청에_쿠키와_세션이_존재하면_302_상태코드를_반환한다() throws IOException {
            String httpRequestMessage = String.join("\r\n",
                    "GET /login HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Cookie: JSESSIONID=f47ac10b-58cc-4372-a567-0e02b2c3d479"
            );
            ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            LoginHandler loginHandler = new LoginHandler();
            Session session = new Session("f47ac10b-58cc-4372-a567-0e02b2c3d479");
            session.setAttribute("user", "user");
            SessionManager.add(session);

            HttpResponse httpResponse = loginHandler.handle(httpRequest);

            String expected = String.join("\r\n",
                    "HTTP/1.1 302 FOUND "
            );
            assertThat(httpResponse.getResponse()).contains(expected);
        }

        @Test
        void POST_요청이_발생하면_302_상태코드와_세션을_반환한다() throws IOException {
            String body = "account=gugu&password=password";
            String httpRequestMessage = String.join("\r\n",
                    "POST /login HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Content-Length: " + body.getBytes().length,
                    "",
                    body
            );
            ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            LoginHandler loginHandler = new LoginHandler();

            HttpResponse httpResponse = loginHandler.handle(httpRequest);

            assertThat(httpResponse.getResponse()).contains("HTTP/1.1 302 FOUND ", "Set-Cookie: JSESSIONID=");
        }

        @ParameterizedTest
        @ValueSource(strings = {"DELETE", "PUT", "PATCH"})
        void GET_POST_요청이_아니면_예외가_발생한다(String method) throws IOException {
            String httpRequestMessage = String.join("\r\n",
                    ""+ method + " /login HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive"
            );
            ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            LoginHandler loginHandler = new LoginHandler();

            assertThatThrownBy(() -> loginHandler.handle(httpRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("일치하는 Method 타입이 없습니다.");
        }
    }
}
