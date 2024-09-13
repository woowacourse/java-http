package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Nested
    @DisplayName("생성 테스트")
    class ConstructorTest {

        @Test
        @DisplayName("요청 생성 테스트")
        void getRequestConstructTest() throws IOException {
            //given
            String requestBody = "requestBody";
            String requestMessage = String.join("\r\n",
                    "POST /target HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Content-Length: " + requestBody.getBytes().length,
                    "",
                    requestBody);

            //when
            HttpRequest request = createRequest(requestMessage);

            //then
            assertAll(
                    () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.POST),
                    () -> assertThat(request.getTarget()).isEqualTo("/target"),
                    () -> assertThat(request.getHttpVersion()).isEqualTo("HTTP/1.1"),
                    () -> assertThat(request.getBody()).isEqualTo(requestBody)
            );
        }
    }

    @Nested
    @DisplayName("세션 생성 테스트")
    class SessionTest {

        @Nested
        @DisplayName("쿠키로 JSESSIONID 를 가지고 있는 HttpRequest 객체는")
        class ContainCookieTest {
            String requestMessage = String.join("\r\n",
                    "GET /target HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Cookie: JSESSIONID=1234",
                    "");

            @Test
            @DisplayName("세션을 조회할 수 있다.")
            void findSessionTest() throws IOException {
                //given
                SessionManager.getInstance().add(new Session("1234"));
                HttpRequest request = createRequest(requestMessage);

                //when
                Session session = request.getSession(false);

                //then
                assertThat(session.getId()).isEqualTo("1234");
            }

            @Test
            @DisplayName("세션을 생성해도 원래의 세션이 조회된다.")
            void createSessionTest() throws IOException {
                //given
                SessionManager.getInstance().add(new Session("1234"));
                HttpRequest request = createRequest(requestMessage);

                //when
                Session session = request.getSession(true);

                //then
                assertThat(session.getId()).isEqualTo("1234");
            }
        }

        @Nested
        @DisplayName("쿠키로 JSESSIONID 를 가지고 있지 않은 HttpRequest 객체는")
        class NotContainCookieTest {
            String requestMessage = String.join("\r\n",
                    "GET /target HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "");

            @Test
            @DisplayName("세션을 조회하면 null 이 반환된다.")
            void findSessionTest() throws IOException {
                //given
                HttpRequest request = createRequest(requestMessage);

                //when
                Session session = request.getSession(false);

                //then
                assertThat(session).isNull();
            }

            @Test
            @DisplayName("세션을 새로 생성할 수 있다.")
            void createSessionTest() throws IOException {
                //given
                HttpRequest request = createRequest(requestMessage);

                //when
                Session session = request.getSession(true);

                //then
                assertThat(session.getId()).isNotNull();
            }
        }
    }

    private HttpRequest createRequest(String httpRequestText) throws IOException {
        InputStream requestStream = new ByteArrayInputStream(httpRequestText.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(requestStream));

        return HttpRequest.readFrom(bufferedReader);
    }
}
