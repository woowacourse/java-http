package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import support.MemoryAppender;
import support.RequestFixture;
import support.StubSocket;

class Http11ProcessorTest {

    private StubSocket stubSocket;
    private MemoryAppender memoryAppender;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(Http11Processor.class);
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory());
        logger.addAppender(memoryAppender);
        logger.setLevel(Level.DEBUG);
        memoryAppender.start();
    }

    @AfterEach
    void afterEach() throws IOException {
        stubSocket.close();
        memoryAppender.reset();
    }

    @Test
    void process() {
        // given
        stubSocket = new StubSocket();
        final var processor = new Http11Processor(stubSocket);

        // when
        processor.process(stubSocket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(stubSocket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = RequestFixture.create(HttpMethod.GET, "/index.html", "");

        stubSocket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(stubSocket);

        // when
        processor.process(stubSocket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        assert resource != null;
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(stubSocket.output()).isEqualTo(expected);
    }

    @Test
    void logUser() {
        // given
        final String httpRequest = RequestFixture.create(HttpMethod.GET, "/login?account=gugu&password=password", "");

        stubSocket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(stubSocket);
        final User expected = InMemoryUserRepository.findByAccount("gugu").orElseThrow();

        // when
        processor.process(stubSocket);

        // then
        assertThat(memoryAppender.contains(expected.toString())).isTrue();
    }

    @Test
    void logUserFail() {
        // given
        final String httpRequest = RequestFixture.create(HttpMethod.GET, "/login?account=gugu&password=uncorrect", "");

        stubSocket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(stubSocket);
        final User expected = InMemoryUserRepository.findByAccount("gugu").orElseThrow();

        // when
        processor.process(stubSocket);

        // then
        assertThat(memoryAppender.contains(expected.toString())).isFalse();
    }
}
