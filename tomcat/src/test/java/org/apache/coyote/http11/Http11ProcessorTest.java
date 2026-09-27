package org.apache.coyote.http11;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.connector.CoyoteAdapter;
import org.junit.jupiter.api.Test;
import support.StubSocket;
import com.techcourse.model.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=existing ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void respondsWithBadRequestWhenRequestLineIsInvalid() {
        final String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1 extra",
                "Host: localhost:8080",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 400 Bad Request ")
                .contains("\r\n\r\nBad Request");
    }

    @Test
    void respondsWithBadRequestWhenRequestBodyIsIncomplete() {
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: 10",
                "",
                "a=b");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 400 Bad Request ")
                .contains("\r\n\r\nBad Request");
    }

    @Test
    void respondsWithInternalServerErrorWhenUnexpectedRequestParsingExceptionOccurs() {
        final var socket = new StubSocket() {
            @Override
            public InputStream getInputStream() {
                return new InputStream() {
                    @Override
                    public int read() {
                        throw new IllegalStateException("요청을 읽을 수 없습니다.");
                    }
                };
            }
        };
        final Http11Processor processor = createProcessor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 500 Internal Server Error ")
                .contains("\r\n\r\nInternal Server Error");
    }

    @Test
    void respondsWithBadRequestWhenFormDataEncodingIsInvalid() {
        final String body = "account=%";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 400 Bad Request ")
                .contains("\r\n\r\nBad Request");
    }

    @Test
    void respondsWithNotFoundWhenStaticResourceDoesNotExist() {
        final String httpRequest = String.join("\r\n",
                "GET /does-not-exist.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 404 Not Found ")
                .contains("\r\n\r\nNot Found");
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=existing ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5571 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginSuccessRedirect() {
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: 30 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Cookie: JSESSIONID=existing ",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found ")
                .contains("Set-Cookie: JSESSIONID=")
                .contains("Location: /index.html ")
                .contains("Content-Length: 0 ");
    }

    @Test
    void loginFailureRedirect() {
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: 27 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Cookie: JSESSIONID=existing ",
                "",
                "account=gugu&password=wrong");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        processor.process(socket);

        final String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /401.html ",
                "Content-Length: 0 ",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void doesNotSetCookieWhenJSessionIdIsMissing() {
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        processor.process(socket);

        assertThat(socket.output()).doesNotContain("Set-Cookie: JSESSIONID=");
    }

    @Test
    void loginPageRedirectsWhenSessionHasUser() {
        final Session session = SessionManager.getInstance().createSession();
        session.setAttribute("user", new User("gugu", "password", "hkkang@woowahan.com"));

        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=" + session.getId() + " ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        processor.process(socket);

        final String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "Content-Length: 0 ",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    private Http11Processor createProcessor(final StubSocket socket) {
        return new Http11Processor(socket, new CoyoteAdapter());
    }
}
