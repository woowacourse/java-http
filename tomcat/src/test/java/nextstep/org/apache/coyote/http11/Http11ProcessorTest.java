package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import support.StubSocket;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        var expected = String.join(
                System.lineSeparator(),
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"
        );

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK " + System.lineSeparator() +
                "Content-Type: text/html;charset=utf-8 " + System.lineSeparator() +
                "Content-Length: 5564 " + System.lineSeparator() +
                System.lineSeparator() +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css_get_요청을_처리한다() throws IOException {
        // given
        String httpRequest = String.join(
                System.lineSeparator(),
                "GET /css/styles.css HTTP/1.1",
                "Accept: text/css,*/*;q=0.1",
                "Accept-Encoding: gzip, deflate, br",
                "Accept-Language: ko-KR,ko;q=0.9",
                "Cache-Control: no-cache",
                "Connection: keep-alive",
                "Host: localhost:8080",
                "Pragma: no-cache",
                "Referer: http://localhost:8080/index.html",
                "Sec-Fetch-Dest: style",
                "Sec-Fetch-Mode: no-cors",
                "Sec-Fetch-Site: same-origin",
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36",
                "sec-ch-ua: \"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Google Chrome\";v=\"116\"",
                "sec-ch-ua-mobile: ?0",
                "sec-ch-ua-platform: \"macOS\""
        );
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        var expected = "HTTP/1.1 200 OK " + System.lineSeparator() +
                "Content-Type: text/css;charset=utf-8 " + System.lineSeparator() +
                "Content-Length: 211991 " + System.lineSeparator() +
                System.lineSeparator() +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 회원가입_요청을_처리한다() throws IOException {
        // given
        String httpRequest = String.join(
                System.lineSeparator(),
                "POST /register HTTP/1.1",
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                "Accept-Encoding: gzip, deflate, br",
                "Accept-Language: ko-KR,ko;q=0.9",
                "Cache-Control: max-age=0",
                "Connection: keep-alive",
                "Content-Length: 55",
                "Content-Type: application/x-www-form-urlencoded",
                "Host: localhost:8080",
                "Origin: http://localhost:8080",
                "Referer: http://localhost:8080/register",
                "Sec-Fetch-Dest: document",
                "Sec-Fetch-Mode: navigate",
                "Sec-Fetch-Site: same-origin",
                "Sec-Fetch-User: ?1",
                "Upgrade-Insecure-Requests: 1",
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36",
                "sec-ch-ua: \"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Google Chrome\";v=\"116\"",
                "sec-ch-ua-mobile: ?0",
                "sec-ch-ua-platform: \"macOS\"",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com"
        );
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).containsAnyOf(
                "HTTP/1.1 302 Found ",
                "Location: /index.html",
                "Set-Cookie: JESSIONID="
        );
    }

    @Test
    void 로그인_POST_요청을_처리한다() throws IOException {
        // given
        String httpRequest = String.join(
                System.lineSeparator(),
                "POST /login HTTP/1.1",
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                "Accept-Encoding: gzip, deflate, br",
                "Accept-Language: ko-KR,ko;q=0.9",
                "Cache-Control: max-age=0",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Cookie: JSESSIONID=9cd99d8d-040c-496d-b54f-d580fed04bd3",
                "Host: localhost:8080",
                "Origin: http://localhost:8080",
                "Referer: http://localhost:8080/login",
                "Sec-Fetch-Dest: document",
                "Sec-Fetch-Mode: navigate",
                "Sec-Fetch-Site: same-origin",
                "Sec-Fetch-User: ?1",
                "Upgrade-Insecure-Requests: 1",
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36",
                "sec-ch-ua: \"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Google Chrome\";v=\"116\"",
                "sec-ch-ua-mobile: ?0",
                "sec-ch-ua-platform: \"macOS\"",
                "",
                "account=gugu&password=password"
        );
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).containsAnyOf(
                "HTTP/1.1 302 Found ",
                "Location: /index.html",
                "Set-Cookie: JESSIONID="
        );
    }

    @Test
    void 로그인_요청을_처리할_때_회원이_아닌_경우_401을_반환한다() throws IOException {
        // given
        String httpRequest = String.join(
                System.lineSeparator(),
                "POST /login HTTP/1.1",
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                "Accept-Encoding: gzip, deflate, br",
                "Accept-Language: ko-KR,ko;q=0.9",
                "Cache-Control: max-age=0",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Cookie: JSESSIONID=9cd99d8d-040c-496d-b54f-d580fed04bd3",
                "Host: localhost:8080",
                "Origin: http://localhost:8080",
                "Referer: http://localhost:8080/login",
                "Sec-Fetch-Dest: document",
                "Sec-Fetch-Mode: navigate",
                "Sec-Fetch-Site: same-origin",
                "Sec-Fetch-User: ?1",
                "Upgrade-Insecure-Requests: 1",
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36",
                "sec-ch-ua: \"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Google Chrome\";v=\"116\"",
                "sec-ch-ua-mobile: ?0",
                "sec-ch-ua-platform: \"macOS\"",
                "",
                "account=none&password=password"
        );
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        var expected = "HTTP/1.1 401 Unauthorized " + System.lineSeparator() +
                "Content-Type: text/html;charset=utf-8 " + System.lineSeparator() +
                "Content-Length: 2426 " + System.lineSeparator() +
                System.lineSeparator() +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 로그인_페이지에_Get요청을_했을_때_유효한_session_id가_있는_경우_index로_리다이렉트_한다() {
        // given
        String sessionId = "9cd99d8d-040c-496d-b54f-d580fed04bd3";
        Session session = new Session(sessionId);
        SessionManager sessionManager = new SessionManager();
        sessionManager.add(session);

        String httpRequest = String.join(
                System.lineSeparator(),
                "GET /login HTTP/1.1",
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                "Accept-Encoding: gzip, deflate, br",
                "Accept-Language: ko-KR,ko;q=0.9",
                "Cache-Control: max-age=0",
                "Connection: keep-alive",
                "Content-Type: application/x-www-form-urlencoded",
                "Cookie: JSESSIONID=" + sessionId
        );
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).containsAnyOf(
                "HTTP/1.1 302 Found ",
                "Location: /index.html"
        );
    }
}
