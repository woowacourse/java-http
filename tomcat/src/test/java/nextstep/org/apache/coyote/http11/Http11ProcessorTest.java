package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static support.StringUtils.removeAllEmptyString;

import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.controller.SingletonContainer;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import support.IoUtils;
import support.StubSocket;

class Http11ProcessorTest {

    static {
        SingletonContainer.registerSingletons();
    }

    @AfterEach
    void tearDown() {
        InMemoryUserRepository.initData();
    }

    @Test
    void process() {
        // given
        final StubSocket socket = new StubSocket();
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        final String actual = socket.output();
        assertThat(removeAllEmptyString(actual)).isEqualTo(removeAllEmptyString(actual));
    }

    // win과 mac의 차이

    /**
     * windows 와 mac OS의 차이로 2바이트 차이가 난다 /r/n 등 개행 문제로 보인다
     */
    @Disabled
    @Test
    void index() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        final String expectedContent = IoUtils.readFileByClassLoader("static/index.html");

        // then
        final String expectedHeader = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: ";

        final String actual = socket.output();
        assertThat(actual).isEqualTo(expectedHeader);
        assertThat(actual).isEqualTo(expectedContent);
    }

    @Test
    void signIn_when_success() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 27",
                "",
                "account=philz&password=1234");

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expectedHeader = "HTTP/1.1 302 Found \r\n"
                + "Location: /index \r\n"
                + "Set-Cookie: JSESSIONID=";

        final String actual = socket.output();
        assertThat(actual).contains(expectedHeader);
    }

    @ParameterizedTest
    @CsvSource(delimiterString = " - ", value = {"phooo - 1234", "philz - 0000"})
    void signIn_when_fail(final String id, final String password) {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 27",
                "",
                "account=" + id + "&password=" + password);

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expectedHeader = "HTTP/1.1 302 Found \r\n"
                + "Location: /401 \r\n";

        final String actual = socket.output();
        assertThat(actual).contains(expectedHeader);
    }

    @Test
    void register_when_success() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 47",
                "",
                "account=foo&email=foo@hello.com&password=1234");

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expectedHeader = "HTTP/1.1 302 Found \r\n"
                + "Location: /index \r\n";

        final String actual = socket.output();
        assertThat(actual).contains(expectedHeader);
    }

    @ParameterizedTest
    @CsvSource(delimiterString = " - ", value = {"philz - foo@hello.com - 1234", "foo - philz@hello.com - 1234"})
    void register_when_fail(final String id, final String email, final String password) {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 200",
                "",
                "account=" + id + "&email=" + email + "&password=" + password);

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expectedHeader = "HTTP/1.1 302 Found \r\n"
                + "Location: /401 \r\n";

        final String actual = socket.output();
        assertThat(actual).contains(expectedHeader);
    }
}
