package http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void HTTP_Request_생성_테스트() throws IOException {
        // given
        String requestBody = "requestBody1\r\nrequestBody2";
        int contentLength = requestBody.getBytes().length;
        String request = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + contentLength,
                "Connection: keep-alive ",
                "",
                "requestBody1",
                "requestBody2");
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(request.getBytes())));

        // when
        HttpRequest httpRequest = HttpRequest.parse(bufferedReader);

        // then
        assertAll(
                () -> assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpRequest.getUrl()).isEqualTo("/index.html"),
                () -> assertThat(httpRequest.getQueryParams().exists()).isFalse(),
                () -> assertThat(httpRequest.getHeaders().getAll()).hasSize(3),
                () -> assertThat(httpRequest.getBody()).isEqualTo(
                        "requestBody1\r\nrequestBody2")
        );
    }

    @Test
    void Accpet_헤더가_있으면_body의_Content_Type으로_반환한다() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css",
                "Connection: keep-alive ",
                "");
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        HttpRequest httpRequest = HttpRequest.parse(bufferedReader);

        // when
        ContentType contentType = httpRequest.getAcceptContentType();

        // then
        assertThat(contentType).isEqualTo(ContentType.TEXT_CSS);
    }

    @Test
    void Accept_헤더가_없으면_확장자를_통해_body의_Content_Type을_반환한다() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET /index.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        HttpRequest httpRequest = HttpRequest.parse(bufferedReader);

        // when
        ContentType contentType = httpRequest.getAcceptContentType();

        // then
        assertThat(contentType).isEqualTo(ContentType.TEXT_CSS);
    }

    @Test
    void 세션을_반환한다() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET /index.css HTTP/1.1 ",
                "Cookie: JSESSIONID=sessionId ",
                "");
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
        Session session = new Session("sessionId");
        SessionManager sessionManager = SessionManager.instance();
        sessionManager.add(session);

        // when
        Session findSession = httpRequest.getSession();

        // then
        assertThat(findSession).isEqualTo(session);
    }
}
