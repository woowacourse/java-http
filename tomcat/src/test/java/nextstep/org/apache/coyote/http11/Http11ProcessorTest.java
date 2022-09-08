package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticFileController;
import org.apache.catalina.ControllerContainer;
import nextstep.jwp.controller.InternalServerExceptionController;
import nextstep.jwp.controller.NotFoundExceptionController;
import org.apache.coyote.ControllerFinder;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import support.MemoryAppender;
import support.RequestFixture;
import support.ResponseFixture;
import support.StubSocket;

class Http11ProcessorTest {

    private StubSocket stubSocket;
    private MemoryAppender memoryAppender;
    private ControllerFinder controllerFinder = new ControllerContainer(
            List.of(new LoginController(), new RegisterController(), new StaticFileController()),
            List.of(new NotFoundExceptionController(), new InternalServerExceptionController())
    );

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
        final var processor = new Http11Processor(stubSocket, controllerFinder);

        // when
        processor.process(stubSocket);

        // then
        var expected = ResponseFixture.create(HttpStatus.OK, "text/html", "Hello world!");

        assertThat(stubSocket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = RequestFixture.create(HttpMethod.GET, "/index.html", "");

        stubSocket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(stubSocket, controllerFinder);

        // when
        processor.process(stubSocket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        assert resource != null;
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = ResponseFixture.create(HttpStatus.OK, "text/html", body);

        assertThat(stubSocket.output()).isEqualTo(expected);
    }
}
